# 开发者文档 · kmp-draco

给**维护 / 共建本仓**的开发者。  
路人看根 [README.md](README.md)；接入工程看 [docs/guide.md](docs/guide.md)。

## 仓库布局

| 路径 | 职责 |
|---|---|
| `kmp-draco/` | KMP 脸：Android / iOS / js actual + cinterop；Ohos 见 `build.ohos.gradle.kts` |
| `kmp-draco-native/` | Rust + `include/` + `build-*.sh` + `dist/` + testdata |
| `ohosInclude/` | Ohos KBA `includeBuild` 入口 |
| `docs/` | guide + adr |

本仓只做 Draco **bitstream → 分属性数组**。不要把 glTF 解析、GPU Present / Session 塞回本仓。

## 本地构建 Native

改 Rust 后 **必须**重跑对应脚本（Gradle / Xcode **不会**自动编 Rust）：

```bash
cd kmp-draco-native
./build-android.sh   # → kmp-draco/.../jniLibs/arm64-v8a/
./build-ios.sh       # → dist/ios/KmpDracoNative.xcframework
./build-ohos.sh      # → dist/ohos/arm64-v8a/  （需 OHOS_SDK_NATIVE）
./build-wasm.sh      # → dist/wasm/  （需 wasm-bindgen-cli 与 Cargo.lock 同版）
```

工具链：

| 端 | 要求 |
|---|---|
| Android | NDK、`cargo-ndk`、`aarch64-linux-android` |
| iOS | Xcode、`aarch64-apple-ios` + `ios-sim` |
| Ohos | OpenHarmony native SDK、`aarch64-unknown-linux-ohos` |
| Wasm | `wasm32-unknown-unknown`、`wasm-bindgen` **0.2.130**（对齐 lock） |

## 测试

```bash
cd kmp-draco-native && cargo test          # decode + C ABI tokyo
./gradlew :kmp-draco:testDebugUnitTest    # commonTest（DecodedMesh 纯 Kotlin）
./gradlew :kmp-draco:compileKotlinIosArm64 :kmp-draco:compileKotlinJs
./gradlew :kmp-draco:connectedDebugAndroidTest   # 需 arm64 设备

# Wasm Node smoke（示例）
cd kmp-draco-native/dist/wasm
# 见 CHANGELOG / 开发机冒烟：tokyo points>0
```

## 发布

渠道状态见 [CHANGELOG.md](CHANGELOG.md)。

```bash
./publish-maven.sh local
```

**本波不上架** CocoaPods / ohpm / npm。Central 另单。

## 边界

| 可以 | 不可以 |
|---|---|
| 扩平台 actual / 多 ABI | 解析 glTF；公开 encode |
| 改进错误 / 体积 | 静默空网格 |
| 上架三渠（新 changelog） | 无 ADR 换 C++ 内核 |

## 下游旁路

- 任意工程：`includeBuild` 本仓根（或已 `publishToMavenLocal`）
- Ohos：`includeBuild(…/ohosInclude)`（KBA 工具链）
