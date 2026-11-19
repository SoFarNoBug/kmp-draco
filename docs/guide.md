# kmp-draco · 接入指南

给**已决定接入**的工程同学。产品定位与最小示例见根 [README.md](../README.md)。构建 / 发版 → [CONTRIBUTING.md](../CONTRIBUTING.md)。

## 前置

1. **Kotlin Multiplatform**（或单端）宿主。
2. 按端打入 **native**（见下表）。只加 Maven、不链/不放 native → 链接失败或 `UnsatisfiedLinkError` / wasm not loaded。
3. 本库**只解比特流**；glTF / Present 由调用方负责。

## 依赖

```kotlin
repositories { mavenLocal(); mavenCentral() }
dependencies {
    implementation("io.github.sofarnobug:kmp-draco:0.1.0")
}
```

| 端 | Kotlin | Native | 接完标准 |
|---|---|---|---|
| **Android** | `kmp-draco` | AAR 内 `jniLibs/arm64-v8a/*.so`（~621 KiB） | `Draco.decode` OK |
| **iOS** | 同上（iosArm64 / iosSimulatorArm64） | 链 [`dist/ios/KmpDracoNative.xcframework`](../kmp-draco-native/dist/ios/) | 链上后 decode OK |
| **Ohos** | `ohosInclude` composite → ohosArm64 | [`dist/ohos/arm64-v8a/*.so`](../kmp-draco-native/dist/ohos/)（~564 KiB） | load 后 decode OK |
| **H5** | js IR | [`dist/wasm`](../kmp-draco-native/dist/wasm/)（~208 KiB wasm） | `__kmpDraco.decode` OK |

### Holm 旁路

- 主仓：`../kmp-draco` 存在 → 默认 `includeBuild`
- Ohos：`includeBuild("../kmp-draco/ohosInclude")`（KBA）
- `kmp.draco.local=false` → Maven

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

与 M0 相同：`Draco.decode` / `DecodedMesh` / `DecodedAttribute` / `AttrType` / `DracoDecodeException`。  
详见源码或仓根 README 示例。`attributeByUniqueId` 对齐 `KHR_draco`。

### H5 加载顺序

1. `<script src="kmp_draco_native.js">`
2. `<script src="kmp_draco_glue.js">`
3. `await wasm_bindgen(wasmUrlOrBytes)`
4. Kotlin `Draco.decode` 或 `__kmpDraco.decode(u8)`

---

## 避坑

1. **x86 模拟器**：无对应 ABI。
2. **整包 GLB**：须先取 Draco bufferView。
3. **多 UV**：用 `attributeByUniqueId`，勿依赖 `texCoords`。
4. **大资产**：同步 decode；勿堵 UI 线程。
5. **iOS/Ohos**：只编 Kotlin 不链 `.a`/`.so` → 链接期失败。
6. **H5**：未 init wasm → `DracoDecodeException("… wasm not loaded")`。

---

## 仓库指路

| 路径 | 职责 |
|---|---|
| [`kmp-draco/`](../kmp-draco/) | Kotlin |
| [`kmp-draco-native/`](../kmp-draco-native/) | Rust + 四脚本 + `dist/` |
| [`ohosInclude/`](../ohosInclude/) | Ohos KBA composite |
| [ADR 0001](adr/0001-independent-rust-bitstream-android.md) / [0002](adr/0002-ios-ohos-wasm-decode.md) | 决策 |
