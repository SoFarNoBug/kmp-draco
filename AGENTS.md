# kmp-draco · AI 协作路由

## 项目概况

- 产品：**kmp-draco**（Draco **bitstream** 解码；Kotlin + Rust `draco-core`）
- 身份：**ADR 0001**（M0 Android）· **ADR 0002**（M1 iOS/Ohos/Wasm）
- Maven：`io.github.sofarnobug:kmp-draco:0.2.0`（Central 已发）；包 `com.jlj.kmp.draco`
- Native：CocoaPods `KmpDracoNative` · ohpm `@jlj/kmp-draco-native` · npm `@sofarnobug/kmp-draco-native`
- 桥：Android JNI · iOS/Ohos C ABI · H5 wasm-bindgen
- 渠道 SoT：[CHANGELOG.md](CHANGELOG.md)；发版：[CONTRIBUTING.md](CONTRIBUTING.md)
- 消费方：Holm ADR 0017（`holm-gltf` 硬依赖）

## 接手阅读顺序

1. 本文件
2. [CONTEXT.md](CONTEXT.md)
3. [ADR 0001](docs/adr/0001-independent-rust-bitstream-android.md) · [ADR 0002](docs/adr/0002-ios-ohos-wasm-decode.md)
4. [README.md](README.md) · [docs/guide.md](docs/guide.md)
5. [CONTRIBUTING.md](CONTRIBUTING.md)

## 硬规则（摘录）

1. 只解比特流 → 分属性数组；禁止做成 glTF 加载器。
2. 公开 API 不暴露 encode。
3. 失败必须抛异常；禁止静默空网格。
4. GPU/Present 走 `kmp-wgpu`，勿扩进本仓。
5. 改身份 / 内核先改 CONTEXT + ADR。

## 仓库地图

| 路径 | 用途 |
|---|---|
| `kmp-draco/` | Kotlin + jniLibs + cinterop |
| `kmp-draco-native/` | Rust + build-*.sh + dist/ + ohpm/ + npm/ |
| `KmpDracoNative.podspec` | CocoaPods |
| `ohosInclude/` | Ohos composite |
| `docs/` | guide + adr |
