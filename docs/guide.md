# kmp-draco · 接入指南

给**已决定接入**的工程同学。产品定位与最小示例见根 [README.md](../README.md)。构建 / 发版 → [开发者文档](../CONTRIBUTING.md)。

## 前置

1. **Kotlin Multiplatform**（或单端）宿主。
2. 按端打入 **native**（见下表）。只加 Maven、不链/不放 native → 链接失败或 `UnsatisfiedLinkError` / wasm not loaded。
3. 本库**只解比特流**；场景图解析、上传 GPU 由调用方负责。

## 各端依赖清单

| 端 | Kotlin | Native | 接完标准 |
|---|---|---|---|
| **Android** | `kmp-draco` | AAR 内 `jniLibs/arm64-v8a/*.so`（~621 KiB） | `Draco.decode` OK |
| **iOS** | 同上（iosArm64 / iosSimulatorArm64） | 链 [`dist/ios/KmpDracoNative.xcframework`](../kmp-draco-native/dist/ios/) | 链上后 decode OK |
| **Ohos** | `ohosInclude` composite → ohosArm64 | [`dist/ohos/arm64-v8a/*.so`](../kmp-draco-native/dist/ohos/)（~564 KiB） | load 后 decode OK |
| **H5** | js IR | [`dist/wasm`](../kmp-draco-native/dist/wasm/)（~208 KiB wasm） | `__kmpDraco.decode` OK |

```kotlin
repositories { mavenLocal(); mavenCentral() }
dependencies {
    implementation("io.github.sofarnobug:kmp-draco:0.1.0")
}
```

### 旁路本仓（可选）

- Gradle `includeBuild("../kmp-draco")`（路径按你的布局）
- Ohos：`includeBuild("…/kmp-draco/ohosInclude")`（KBA）
- 也可继续走 mavenLocal / 日后 Central

### 包体积（native）

| 产物 | 约 |
|---|---|
| Android arm64 `.so` | ~621 KiB |
| Ohos arm64 `.so` | ~564 KiB |
| H5 `_bg.wasm` | ~208 KiB |
| iOS xcframework（device+sim） | 数十 MB 级（含双切片 staticlib） |

---

## 概念

```text
ByteArray（Draco bitstream）
    │  Draco.decode（同步；调度由调用方负责）
    ▼
DecodedMesh（分属性 + indices）
```

包名：`com.jlj.kmp.draco`。

桥：

| 端 | 桥 |
|---|---|
| Android | JNI |
| iOS / Ohos | C ABI（`kmp_draco_native.h`） |
| H5 | wasm-bindgen + `window.__kmpDraco` |

---

## API

### `object Draco`

| 成员 | 说明 |
|---|---|
| `fun decode(bytes: ByteArray): DecodedMesh` | 同步解码。失败抛 `DracoDecodeException` |

### `DecodedMesh`

| 成员 | 说明 |
|---|---|
| `numPoints: Int` | 顶点数 |
| `indices: IntArray` | 三角索引 |
| `attributes: Array<DecodedAttribute>` | 分属性列表 |
| `attributeByUniqueId(uniqueId: Int)` | 按 Draco uniqueId 查找（对齐 `KHR_draco` 映射） |
| `named(attrType: Int)` | 按 `AttrType` 取**首个** |
| `positions` | POSITION 的 `values`；缺失则抛 |
| `normals` / `texCoords` / `colors` | 有则返回；无则空 `FloatArray` |

### `DecodedAttribute`

| 成员 | 说明 |
|---|---|
| `uniqueId` / `attrType` / `numComponents` | 元数据 |
| `values: FloatArray` | 展开后的浮点缓冲（长度 ≈ `numPoints * numComponents`） |

### `DecodedMesh.AttrType`

`POSITION=0` · `NORMAL=1` · `COLOR=2` · `TEX_COORD=3` · `GENERIC=4`

### `DracoDecodeException`

空缓冲、非 mesh、无 POSITION、H5 wasm 未加载等显式失败。

### 示例

```kotlin
val mesh = Draco.decode(dracoBytes)
val pos = mesh.positions
val nrm = mesh.attributeByUniqueId(1) // 用 extension 里的 uniqueId
```

### H5 加载顺序

1. `<script src="kmp_draco_native.js">`
2. `<script src="kmp_draco_glue.js">`
3. `await wasm_bindgen(wasmUrlOrBytes)`
4. Kotlin `Draco.decode` 或 `__kmpDraco.decode(u8)`

---

## 避坑

1. **x86 模拟器**：无对应 ABI。
2. **整包 GLB / glTF**：须先取 Draco bufferView。
3. **多 UV**：用 `attributeByUniqueId`，勿依赖 `texCoords`。
4. **大资产**：同步 decode；勿堵 UI 线程。
5. **iOS/Ohos**：只编 Kotlin 不链 `.a`/`.so` → 链接期失败。
6. **H5**：未 init wasm → `DracoDecodeException("… wasm not loaded")`。

---

## 仓库指路

| 路径 | 职责 |
|---|---|
| [`kmp-draco/`](../kmp-draco/) | Kotlin 模块 |
| [`kmp-draco-native/`](../kmp-draco-native/) | Rust + 四脚本 + `dist/` |
| [`ohosInclude/`](../ohosInclude/) | Ohos KBA composite |
| [ADR 0001](adr/0001-independent-rust-bitstream-android.md) / [0002](adr/0002-ios-ohos-wasm-decode.md) | 决策 |
