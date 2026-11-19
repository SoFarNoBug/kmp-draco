# kmp-draco · 接入指南

给**已决定接入**的工程同学。产品定位与最小示例见根 [README.md](../README.md)。构建 / 发版 → [CONTRIBUTING.md](../CONTRIBUTING.md)。

## 前置

1. **Kotlin Multiplatform**（或单端 Kotlin / Android）宿主。
2. 按端打入 **native**。M0 仅 Android **`arm64-v8a`** 的 `libkmp_draco_native.so`。只加 Maven、不带 native → `UnsatisfiedLinkError`。
3. 本库**只解比特流**；解析 glTF / 拼 IR / 上 GPU 由调用方（如 `holm-gltf`）负责。

## 依赖

```kotlin
repositories {
    mavenCentral() // 上架后
    mavenLocal()
}

dependencies {
    implementation("io.github.sofarnobug:kmp-draco:0.1.0")
}
```

| 端 | Maven | Native | 接完标准 |
|---|---|---|---|
| **Android** | `kmp-draco` | AAR 内 `jniLibs/arm64-v8a/libkmp_draco_native.so`（~621 KiB） | `Draco.decode` 对合法 `.drc` / Draco bufferView 返回 `DecodedMesh` |
| **iOS / Ohos / H5** | 同坐标（脸可编） | **M0 无** | 调 decode 会链失败 / 须上层 fail-loud |

### Holm 旁路

KuiklyHolm：`../kmp-draco` 存在则默认 `includeBuild`；`local.properties` / `-Pkmp.draco.local=false` 走 Maven。见 [Holm ADR 0017](../../KuiklyHolm/docs/adr/0017-kmp-draco-under-holm-gltf.md)。

### 包体积

体积主要来自 **native**，不是 Kotlin 薄脸：

| 产物 | 约 |
|---|---|
| Android arm64 `libkmp_draco_native.so`（release） | **~621 KiB** |

多 ABI 本波未出；x86 模拟器会缺库。

---

## 概念

```text
ByteArray（Draco bitstream / bufferView 载荷）
    │  Draco.decode  （同步；调度由调用方负责）
    ▼
DecodedMesh
    ├─ numPoints
    ├─ indices: IntArray
    └─ attributes[]: DecodedAttribute
           uniqueId · attrType · numComponents · values: FloatArray
```

- **输入**：压缩几何字节。**不是**整个 `.glb`。
- **输出**：分属性、非交错；失败抛 `DracoDecodeException`（禁止静默空网格）。
- **KHR_draco**：用 extension 里的 `attributes` 映射（语义名 → `uniqueId`）调用 `attributeByUniqueId`；不要假设 `texCoords` 一定是 TEXCOORD_0。

包名：`com.jlj.kmp.draco`。

---

## API

### `object Draco`

| 成员 | 说明 |
|---|---|
| `decode(bytes: ByteArray): DecodedMesh` | 同步解码；失败抛 `DracoDecodeException` |

### `class DecodedMesh`

| 成员 | 说明 |
|---|---|
| `numPoints: Int` | 顶点数 |
| `indices: IntArray` | 三角索引（无索引时实现可能给出平凡序列，以实测为准） |
| `attributes: Array<DecodedAttribute>` | 全部属性 |
| `attributeByUniqueId(id)` | 按 Draco uniqueId 查找（**KHR_draco 推荐**） |
| `named(attrType)` | 按 `AttrType` 取**第一个**匹配 |
| `positions` | POSITION；缺失则抛异常 |
| `normals` / `texCoords` / `colors` | 可能为空数组；`texCoords` = 首个 TEX_COORD（多 UV 不保证顺序） |

### `DecodedMesh.AttrType`

| 常量 | 值 | 含义 |
|---|---|---|
| `POSITION` | 0 | 位置（必有） |
| `NORMAL` | 1 | 法线 |
| `COLOR` | 2 | 颜色 |
| `TEX_COORD` | 3 | 纹理坐标 |
| `GENERIC` | 4 | 其它 |

### `class DecodedAttribute`

| 字段 | 说明 |
|---|---|
| `uniqueId` | 对齐 `KHR_draco_mesh_compression.attributes` |
| `attrType` | 见 `AttrType` |
| `numComponents` | 如 POSITION=3、UV=2 |
| `values` | 紧挨的 `FloatArray`（`numPoints * numComponents`） |

### `class DracoDecodeException`

解码失败（空缓冲、非 triangle mesh、无 POSITION、内核拒绝等）。消息可读，供上层 `onError` / 日志。

---

## 最小示例

```kotlin
val mesh = Draco.decode(bytes)
val positions = mesh.positions
val normals = mesh.normals
// glTF：按 extension 映射
val uv = mesh.attributeByUniqueId(uvUniqueId)?.values ?: FloatArray(0)
```

仪器冒烟（需 arm64 设备）：

```bash
./gradlew :kmp-draco:connectedDebugAndroidTest
```

夹具：`kmp-draco-native/testdata/tokyo_prim0.drc`（LittlestTokyo  primitive 0）。

---

## 避坑

1. **x86 模拟器**：M0 无对应 `.so` → `UnsatisfiedLinkError`。用 arm64 真机 / 臂模拟器。
2. **整包 GLB 丢进 decode**：会失败。先取出 Draco bufferView 字节。
3. **多套 UV**：`texCoords` 只取第一个 `TEX_COORD`；生产路径用 `attributeByUniqueId`。
4. **主线程解码大资产**：Tokyo 级可能数百 ms～数秒；调度（后台线程）由调用方负责，本库同步不帮你切线程。
5. **非 Android 端**：在补齐 native 前，含 Draco 的资产应在上层 fail-loud，不要假装已解。

---

## 仓库指路

| 路径 | 职责 |
|---|---|
| [`kmp-draco/`](../kmp-draco/) | Kotlin 模块 |
| [`kmp-draco-native/`](../kmp-draco-native/) | Rust + `build-android.sh` + testdata |
| [CONTEXT.md](../CONTEXT.md) | 领域语言 |
| [docs/adr/0001-…](adr/0001-independent-rust-bitstream-android.md) | 身份决策 |
| 消费方 | Holm [`ADR 0017`](../../KuiklyHolm/docs/adr/0017-kmp-draco-under-holm-gltf.md) · [`holm-gltf`](../../KuiklyHolm/holm-gltf) |
