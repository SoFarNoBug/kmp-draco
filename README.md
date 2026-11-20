# kmp-draco

**Draco 比特流进，分属性数组出。**

Kotlin Multiplatform 的 **Draco bitstream** 解码原语：业务侧同一套 Kotlin API，底层是 Rust [`draco-core`](https://crates.io/crates/draco-core)（decoder-only）。  
**不是** glTF / GLB 加载器，**不**做 encode，**不**产出 GPU buffer。

你可以用它：

- 把 `KHR_draco_mesh_compression` 的 bufferView 字节（或裸 `.drc`）解成 POSITION / NORMAL / UV / COLOR + indices
- 在自己的网格管线里按 `uniqueId` 对齐 extension 属性映射

| | |
|---|---|
| Maven | `io.github.sofarnobug:kmp-draco:0.2.0`（**Central 已发**） |
| Native | Android `.so`（AAR）· CocoaPods `KmpDracoNative` `0.2.0`（**trunk 已收**）· ohpm `@jlj/kmp-draco-native@0.2.0`（**审核中**）· npm `@sofarnobug/kmp-draco-native@0.2.0`（**已发**） |
| 语言 | Kotlin Multiplatform 脸 + Rust / `draco-core` |
| License | Apache-2.0（对齐 `draco-core`） |

> **路人读本页。** 完整接入 / API 全表 → [docs/guide.md](docs/guide.md)。构建 / 发版 / 渠道 → [开发者文档](CONTRIBUTING.md) · [CHANGELOG](CHANGELOG.md)。

---

## 为什么是独立仓？

Draco 是**压缩几何格式**：输入是一段比特流，输出是 CPU 侧属性数组。把它和场景图解析、GPU Present 捆在同一库里会糊边界。  
本仓只做一件事——**bitstream → 分属性缓冲**；形态是独立仓 + Kotlin 脸 + Rust 内核。

```text
你的 KMP / App
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
| Pod / ohpm / npm 上架 | **0.2.0** | 见 CHANGELOG 渠道表 |
| Point cloud / encode / 交错 VBO / 解析 glTF | **非目标** | ADR 0001 |

---

## 最小接入

```kotlin
dependencies {
    implementation("io.github.sofarnobug:kmp-draco:0.2.0")
}
```

```kotlin
val mesh = Draco.decode(dracoBytes)
val pos = mesh.positions
```

**各端还须带 native**（步骤与避坑 → [docs/guide.md](docs/guide.md)）：

| 端 | 怎么带 |
|---|---|
| Android | AAR 已含 `jniLibs/arm64-v8a` |
| iOS | `pod 'KmpDracoNative', '0.2.0'`（请用 **xcworkspace**） |
| Ohos | `ohpm install @jlj/kmp-draco-native@0.2.0` |
| H5 | `npm i @sofarnobug/kmp-draco-native@0.2.0`（或拷贝 js + wasm + glue） |

旁路开发：Gradle `includeBuild` 本仓；Ohos 用 [`ohosInclude/`](ohosInclude/)。

---

## 常见问题（路人）

**Q. 能直接丢整个 `.glb` 吗？**  
A. 不能。先取出 Draco bufferView 字节，再 `Draco.decode`。

**Q. 多套 UV 用哪个 API？**  
A. `attributeByUniqueId`；不要依赖 `texCoords`（只取首个 `TEX_COORD`）。

**Q. 详细接入、API 表、避坑去哪看？**  
A. → [docs/guide.md](docs/guide.md)。编 Rust / 发版 → [开发者文档](CONTRIBUTING.md)。

---

## 仓库指路

| 文档 | 给谁 |
|---|---|
| [docs/guide.md](docs/guide.md) | 接入工程 |
| [CONTRIBUTING.md](CONTRIBUTING.md) | 维护本仓 |
| [CHANGELOG.md](CHANGELOG.md) | 版本与渠道 |
| [CONTEXT.md](CONTEXT.md) | 领域语言 |
| [docs/adr/](docs/adr/) | 决策记录 |
| [`kmp-draco/`](kmp-draco/) | Kotlin 模块说明 |
| [`kmp-draco-native/`](kmp-draco-native/) | Rust 内核说明 |

接入细节与避坑 → **[docs/guide.md](docs/guide.md)**  
构建 / 发版 / 渠道 → **[开发者文档](CONTRIBUTING.md)** · **[CHANGELOG](CHANGELOG.md)**

## License

Apache-2.0.
