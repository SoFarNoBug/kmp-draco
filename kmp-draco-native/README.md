# kmp-draco-native（Rust）

Crate：`kmp_draco_native`（`cdylib` + `rlib` + `staticlib`）  
内核：[`draco-core` 2.3.3](https://crates.io/crates/draco-core) decoder-only features。

## 职责

- `decode_mesh`：比特流 → 分属性 `f32` + indices
- Android：`android_jni.rs`
- iOS / Ohos：`c_api.rs` + [`include/kmp_draco_native.h`](include/kmp_draco_native.h)
- H5：`wasm_exports.rs`（wasm-bindgen）
- **不**懂 glTF；**不**产出 GPU 资源

## 构建

```bash
./build-android.sh   # → ../kmp-draco/src/androidMain/jniLibs/arm64-v8a/
./build-ios.sh       # → dist/ios/KmpDracoNative.xcframework
./build-ohos.sh      # → dist/ohos/arm64-v8a/libkmp_draco_native.so
./build-wasm.sh      # → dist/wasm/（js + wasm + glue）
```

## 测试

```bash
cargo test   # tokyo + C ABI roundtrip
```

夹具：[`testdata/tokyo_prim0.drc`](testdata/tokyo_prim0.drc)。

## 非目标

- 上架 Pod/ohpm/npm（见 ADR 0002）
- Google C++ 内核（ADR 0001）
