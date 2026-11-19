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
| Native | Android `.so` · iOS xcframework · Ohos `.so` · H5 wasm（**本地 dist/**；三渠上架另单） |
| 语言 | Kotlin Multiplatform 脸 + Rust / `draco-core` |
| License | Apache-2.0（对齐 `draco-core`） |

> **路人读本页。** 完整接入 / API 全表 → [docs/guide.md](docs/guide.md)。构建 / 发版 → [CONTRIBUTING.md](CONTRIBUTING.md) · [CHANGELOG](CHANGELOG.md)。身份 SoT → [CONTEXT.md](CONTEXT.md) · [ADR 0001](docs/adr/0001-independent-rust-bitstream-android.md) · [ADR 0002](docs/adr/0002-ios-ohos-wasm-decode.md)。

---

## 为什么是独立仓？

Draco 是**压缩几何格式**，不是 GPU 原语。把它塞进 `kmp-wgpu` 或 Holm core 会污染边界。形态对齐 [`kmp-wgpu`](../kmp-wgpu)：独立仓 + Kotlin 脸 + Rust 内核。

Holm 侧消费约定见 [KuiklyHolm ADR 0017](../KuiklyHolm/docs/adr/0017-kmp-draco-under-holm-gltf.md)。

```text
你的 KMP / App / holm-gltf
    │  Kotlin API（com.jlj.kmp.draco）
    ▼
kmp-draco
    │  Android JNI · iOS/Ohos C ABI · H5 wasm-bindgen
    ▼
kmp_draco_native（Rust + draco-core decoder）
    ├─ Android ──► libkmp_draco_native.so（arm64-v8a）
    ├─ iOS ──────► KmpDracoNative.xcframework
    ├─ Ohos ─────► libkmp_draco_native.so（arm64-v8a）
    └─ H5 ───────► kmp_draco_native.js + _bg.wasm
```

---

## 本库交到哪一步？

| 场景 | 状态 | 说明 |
|---|---|---|
| Triangle mesh bitstream → 分属性 `FloatArray` + indices | **已有** | `Draco.decode` |
| `KHR_draco` uniqueId 属性查找 | **已有** | `DecodedMesh.attributeByUniqueId` |
| Android arm64 | **已有** | Tokyo fixture + instrumented test |
| iOS arm64 device+sim | **已有（本地）** | xcframework + cinterop；App 须链框架 |
| Ohos arm64 | **已有（本地）** | `.so` + `ohosInclude`；App 须放 libs |
| H5 wasm | **已有（本地）** | `window.__kmpDraco`；宿主先 init wasm |
| Pod / ohpm / npm 上架 | **未做** | ADR 0002 |
| Point cloud / encode / 交错 VBO / 解析 glTF | **非目标** | ADR 0001 |

---

## 怎么用

```kotlin
dependencies {
    implementation("io.github.sofarnobug:kmp-draco:0.1.0")
}
```

```kotlin
val mesh = Draco.decode(dracoBytes)
val pos = mesh.positions
```

**各端还须带 native**（详见 [guide](docs/guide.md)）：

| 端 | 怎么带 |
|---|---|
| Android | AAR 已含 `jniLibs/arm64-v8a` |
| iOS | 链 `kmp-draco-native/dist/ios/KmpDracoNative.xcframework` |
| Ohos | 拷 `dist/ohos/arm64-v8a/libkmp_draco_native.so` 到 `entry/libs` |
| H5 | 加载 `dist/wasm/*.js` + `_bg.wasm`，`await wasm_bindgen(...)` 后用 `__kmpDraco` |

### Holm / 旁路仓

`../kmp-draco` 存在则默认 `includeBuild`；`kmp.draco.local=false` 走 Maven。Ohos 用 `ohosInclude/` composite。

---

## 仓库指路

| 文档 | 给谁 |
|---|---|
| [docs/guide.md](docs/guide.md) | 接入工程 |
| [CONTRIBUTING.md](CONTRIBUTING.md) | 维护本仓 |
| [CHANGELOG.md](CHANGELOG.md) | 版本与渠道 |
| [CONTEXT.md](CONTEXT.md) | 领域语言 |
| [docs/adr/](docs/adr/) | 决策记录 |

## License

Apache-2.0.
