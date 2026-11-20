# ADR 0002: M1 · iOS / Ohos / Wasm decode

- Status: Accepted
- Date: 2026-11-19

## Context

ADR 0001 锁定 M0 仅 Android JNI。Android 链路（含 Holm `KHR_draco`）已通。需在同一 Rust `decode_mesh` 上补齐 iOS / Ohos / H5，形态对齐 `kmp-wgpu`（C ABI + 脚本产物 + KMP actual），且本波不上架 Pod/ohpm/npm。

## Decision

1. **共享 C ABI**（手写 `include/kmp_draco_native.h` + `c_api.rs`）：opaque mesh、thread-local `last_error`、`abi_version=1`；供 **iOS / Ohos** cinterop。Android **保留 JNI**，不经 C。
2. **Wasm**：`wasm_bindgen` 导出（`kmpDracoDecode` 等），**不用** C 头；宿主加载 `dist/wasm` + `kmp_draco_glue.js` → `window.__kmpDraco`。
3. **产物脚本**：`build-ios.sh` → `KmpDracoNative.xcframework`；`build-ohos.sh` → `dist/ohos/arm64-v8a/*.so`；`build-wasm.sh` → js+wasm。ABI：各端 **arm64 / wasm32** only。
4. **KMP**：主工程 `iosArm64` + `iosSimulatorArm64` + `js`；Ohos 经 `ohosInclude/` + `build.ohos.gradle.kts`（KBA）。
5. **链接责任**：Kotlin 脸不自带链 native；App 链 xcframework / 放 `.so` / 拷 wasm。
6. **本波不上架** CocoaPods trunk / ohpm / npm；Central 发版另单。

## Consequences

- 仓内可入库 `dist/` 产物便于 includeBuild 狗粮（体积：iOS xcframework 较大、Ohos ~564 KiB、wasm ~208 KiB）。
- Holm `DracoBitstream` 非 Android actual 仍可 fail-loud，直到消费方接线（非本 ADR 范围）。
- 缺 OH SDK / Xcode 时脚本失败不挡其它端合入。

## Aftermath（0.2.0 · 2026-11-20）

M1 当时「本波不上架」已由 **0.2.0** 补齐远端：Maven Central `kmp-draco:0.2.0`、CocoaPods `KmpDracoNative`、npm `@sofarnobug/kmp-draco-native`；ohpm `@jlj/kmp-draco-native` 已提交审核。渠道 SoT → 根 [CHANGELOG.md](../../CHANGELOG.md)。

## Relates

- [ADR 0001](0001-independent-rust-bitstream-android.md)
- [KuiklyHolm ADR 0017](../../../KuiklyHolm/docs/adr/0017-kmp-draco-under-holm-gltf.md)
