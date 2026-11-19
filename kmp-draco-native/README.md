# kmp-draco-native（Rust）

Crate：`kmp_draco_native`（`cdylib` + `rlib`）  
内核：[`draco-core` 2.3.3](https://crates.io/crates/draco-core)，features：`decoder` + `edgebreaker_valence_decode` + `legacy_bitstream_decode`（**无 encode**）。

## 职责

- `MeshDecoder` 路径：比特流 → 每属性 `f32` + indices
- Android：`jni` 桥，导出供 Kotlin `DracoPlatform` 调用
- **不**懂 glTF；**不**绑 Holm

## 构建

```bash
./build-android.sh
```

产物拷到：

`../kmp-draco/src/androidMain/jniLibs/arm64-v8a/libkmp_draco_native.so`

要求：`ANDROID_NDK_HOME`、`cargo-ndk`、`aarch64-linux-android`。Release 配置见 `Cargo.toml`（`opt-level = "s"`、LTO、strip）。

## 测试

```bash
cargo test
```

夹具：[`testdata/tokyo_prim0.drc`](testdata/tokyo_prim0.drc)（LittlestTokyo 某 primitive 的 Draco 载荷）。

## 非目标

- 多平台（iOS / Ohos / wasm）脚本 — 后补
- 把 Google C++ Draco 树引进本 crate（身份冲突，见 ADR 0001）
