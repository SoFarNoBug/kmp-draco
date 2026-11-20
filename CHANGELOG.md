# Changelog

## Unreleased

- [FIX] jsMain `toUint8Array`：勿把 `Uint8Array` 当函数调用（`(intermediate value) is not a function`）；按无符号字节写入后再 `__kmpDraco.decode`

### 0.1.1 — 2026-11-19（M1 三端 decode）

- [ADD] iOS：`build-ios.sh` → `KmpDracoNative.xcframework`（device+sim arm64）+ `iosMain` cinterop
- [ADD] Ohos：`build-ohos.sh` → `dist/ohos/arm64-v8a/libkmp_draco_native.so` + `ohosInclude` / `ohosArm64Main`
- [ADD] H5：`build-wasm.sh` → `dist/wasm`（js + wasm + glue）+ `jsMain` → `window.__kmpDraco`
- [ADD] 共享 C ABI：`include/kmp_draco_native.h` + `c_api.rs`（Android 仍走 JNI）
- [ADD] ADR 0002
- 本波 **不上架** Pod / ohpm / npm

### 分发渠道（更新）

| 渠道 | 状态 |
|---|---|
| mavenLocal `kmp-draco` | 可用（Kotlin 脸；native 另链） |
| Maven Central | 未发 |
| iOS xcframework（仓内 `dist/ios`） | **本地可用** |
| Ohos `.so`（仓内 `dist/ohos`） | **本地可用** |
| H5 wasm（仓内 `dist/wasm`） | **本地可用**（Node smoke tokyo OK） |
| CocoaPods / ohpm / npm 上架 | **未做** |

## 0.1.0 — 2026-11-18

首发 M0：Android arm64 Draco **bitstream** 同步解码；Kotlin 脸 + Rust `draco-core` 内核。

### 能力

- `Draco.decode(ByteArray) → DecodedMesh`；失败 `DracoDecodeException`
- 分属性输出：`POSITION`（必有）/ `NORMAL` / `TEX_COORD` / `COLOR` / `GENERIC` + indices
- `attributeByUniqueId` 对齐 `KHR_draco_mesh_compression` 映射
- Native：`libkmp_draco_native.so`（arm64-v8a，release ~621 KiB）
- 测试：`kmp-draco-native/testdata/tokyo_prim0.drc`（`cargo test`）+ `connectedDebugAndroidTest`

### 分发渠道

| 渠道 | 坐标 / 包 | 状态 |
|---|---|---|
| mavenLocal | `io.github.sofarnobug:kmp-draco:0.1.0` | **可用**（`./publish-maven.sh local`） |
| Maven Central | 同上 | **未发**（另单） |
| iOS / ohpm / npm native | — | **无**（M0 仅 Android） |

### 边界（故意不做）

- 不解 glTF/GLB；不 encode；无 point cloud；无 x86 `.so`
