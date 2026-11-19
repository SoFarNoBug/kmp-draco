# kmp-draco

**Draco 比特流进，分属性数组出。**

Kotlin Multiplatform 的 **Draco bitstream** 解码原语：业务侧同一套 Kotlin API，底层是 Rust [`draco-core`](https://crates.io/crates/draco-core)（decoder-only）。  
**不是** glTF / GLB 加载器，**不是** Holm 的一部分，**不**做 encode。

你可以用它：

- 把 `KHR_draco_mesh_compression` 的 bufferView 字节（或裸 `.drc`）解成 POSITION / NORMAL / UV / COLOR + indices
- 在自己的网格 / glTF 管线里按 `uniqueId` 对齐 extension 属性映射

| | |
|---|---|
| Maven | `io.github.sofarnobug:kmp-draco:0.1.0`（**mavenLocal 可用**；Central 另单） |
| Native | Android `arm64-v8a` `libkmp_draco_native.so`（~621 KiB release） |
| 语言 | Kotlin Multiplatform 脸 + Rust / `draco-core` |
| License | Apache-2.0（对齐 `draco-core`） |

> **路人读本页。** 完整接入 / API 全表 → [docs/guide.md](docs/guide.md)。构建 / 发版 → [CONTRIBUTING.md](CONTRIBUTING.md) · [CHANGELOG](CHANGELOG.md)。身份 SoT → [CONTEXT.md](CONTEXT.md) · [ADR 0001](docs/adr/0001-independent-rust-bitstream-android.md)。

---

## 为什么是独立仓？

Draco 是**压缩几何格式**，不是 GPU 原语。把它塞进 `kmp-wgpu` 或 Holm core 会污染边界（GL 轨也要解、wgpu 不懂 bitstream）。形态对齐 [`kmp-wgpu`](../kmp-wgpu)：独立仓 + Kotlin 脸 + Rust 内核。

Holm 侧消费约定见 [KuiklyHolm ADR 0017](../KuiklyHolm/docs/adr/0017-kmp-draco-under-holm-gltf.md)（`holm-gltf` **硬依赖**本库，fail-loud）。

```text
你的 KMP / App / holm-gltf
    │  Kotlin API（com.jlj.kmp.draco）
    ▼
kmp-draco
    │  JNI（M0：Android）
    ▼
kmp_draco_native（Rust + draco-core decoder）
    │
    └─ Android ──► libkmp_draco_native.so（arm64-v8a）
```

---

## 本库交到哪一步？

| 场景 | 状态 | 说明 |
|---|---|---|
| Triangle mesh bitstream → 分属性 `FloatArray` + indices | **已有（M0）** | `Draco.decode` |
| `KHR_draco` uniqueId 属性查找 | **已有** | `DecodedMesh.attributeByUniqueId` |
| Android arm64 真机 / 仪器测试 | **已有** | Tokyo fixture + `connectedDebugAndroidTest` |
| iOS / Ohos / Wasm native | **规划中** | M0 不承诺；缺库须 fail-loud |
| Point cloud / encode / 交错 VBO | **非目标** | 见 ADR 0001 |
| 解析 `.gltf` / `.glb` / 场景图 | **非目标** | 交给 `holm-gltf` 等上层 |

---

## 怎么用

```kotlin
repositories {
    mavenCentral() // Central 上架后
    mavenLocal()   // 狗粮：先 ./publish-maven.sh local
}

dependencies {
    implementation("io.github.sofarnobug:kmp-draco:0.1.0")
}
```

```kotlin
import com.jlj.kmp.draco.Draco
import com.jlj.kmp.draco.DracoDecodeException

try {
    val mesh = Draco.decode(dracoBytes)
    val pos = mesh.positions          // FloatArray，必有
    val nrm = mesh.normals            // 可能为空
    val uv0 = mesh.attributeByUniqueId(texCoordUniqueId)?.values
} catch (e: DracoDecodeException) {
    // 空缓冲 / 非 mesh / 无 POSITION 等
}
```

**Native 必带**：只加 Maven、不带 `jniLibs/**/libkmp_draco_native.so` → `UnsatisfiedLinkError`。  
本仓 AAR 已打入 `arm64-v8a`；**x86 模拟器本波不支持**。

### Holm / 旁路仓

旁路仓 `../kmp-draco` **存在则默认** `includeBuild`；`kmp.draco.local=false` 才解析 Maven（可先 `publish-maven.sh local`）。键名对齐 `kmp.wgpu.local`。

---

## 模块怎么选

M0 **单 artifact**：

| 路径 | 你得到什么 |
|---|---|
| **`:kmp-draco`**（Maven `kmp-draco`） | Kotlin API + 打入的 Android `.so` |
| **`kmp-draco-native/`** | Rust crate（不单独发 Maven；随 AAR / 源码构建） |

---

## 仓库指路

| 文档 | 给谁 |
|---|---|
| [docs/guide.md](docs/guide.md) | 接入工程 |
| [CONTRIBUTING.md](CONTRIBUTING.md) | 维护本仓 |
| [CHANGELOG.md](CHANGELOG.md) | 版本与渠道 |
| [CONTEXT.md](CONTEXT.md) | 领域语言 / 边界 |
| [docs/adr/](docs/adr/) | 决策记录 |
| [kmp-draco/README.md](kmp-draco/README.md) | Kotlin 模块面 |
| [kmp-draco-native/README.md](kmp-draco-native/README.md) | Rust 内核 |

## License

Apache-2.0.
