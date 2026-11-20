# 开发者文档 · kmp-draco

给**维护 / 共建本仓**的开发者。  
路人看根 [README.md](README.md)；接入工程看 [docs/guide.md](docs/guide.md)。

## 仓库布局

| 路径 | 职责 |
|---|---|
| `kmp-draco/` | KMP 脸：Android / iOS / js actual + cinterop；Ohos 见 `build.ohos.gradle.kts` |
| `kmp-draco-native/` | Rust + `include/` + `build-*.sh` + `dist/` + testdata |
| `kmp-draco-native/ohpm/` | 鸿蒙 ohpm 包 `@jlj/kmp-draco-native` |
| `kmp-draco-native/npm/` | H5 npm 包 `@sofarnobug/kmp-draco-native` |
| `KmpDracoNative.podspec` | CocoaPods（仓根） |
| `ohosInclude/` | Ohos KBA `includeBuild` 入口 |
| `docs/` | guide + adr |

本仓只做 Draco **bitstream → 分属性数组**。不要把 glTF 解析、GPU Present / Session 塞回本仓。

## 本地构建 Native

改 Rust 后 **必须**重跑对应脚本（Gradle / Xcode **不会**自动编 Rust）：

```bash
cd kmp-draco-native
./build-android.sh   # → kmp-draco/.../jniLibs/arm64-v8a/
./build-ios.sh       # → dist/ios/KmpDracoNative.xcframework
./build-ohos.sh      # → dist/ohos/ + ohpm/src/main/  （需 OHOS_SDK_NATIVE）
./build-wasm.sh      # → dist/wasm/ + npm/  （需 wasm-bindgen-cli 与 Cargo.lock 同版）
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

**渠道状态**见根 [CHANGELOG.md](CHANGELOG.md)。发版前先打 **同版本 git tag**（CocoaPods `s.source` 按 tag 拉仓）；xcframework / ohpm `src/main` / npm 产物须进 git。

### Maven Central

```bash
./publish-maven.sh          # Central（需凭证）
./publish-maven.sh local    # mavenLocal 狗粮
```

坐标：`io.github.sofarnobug:kmp-draco`。

### 鸿蒙 ohpm

```bash
cd kmp-draco-native/ohpm
./publish-ohos.sh
```

口令经 hideable 读取时，**整段 `send "pass\r"` 会因控制字符 `\p{Cc}` 被拒**；脚本用 `_publish.expect` **逐字符**喂入口令。

### iOS CocoaPods

- podspec：**仓根** `KmpDracoNative.podspec`
- xcframework：`build-ios.sh` → 入库 `kmp-draco-native/dist/ios/`
- iOS 部署下限 **15.0**；模拟器仅 arm64（排除 `x86_64`）
- 发布：

```bash
pod trunk push KmpDracoNative.podspec --allow-warnings
```

### H5 npm

```bash
cd kmp-draco-native/npm
npm login
npm publish --access public
```

包名 `@sofarnobug/kmp-draco-native`（npm org=`sofarnobug`；ohpm 仍为 `@jlj/...`）。

### GitHub Release（可选）

tag 推送后可 `gh release create 0.2.0`；与 Maven / Pod 无硬依赖。

## 边界

| 可以 | 不可以 |
|---|---|
| 扩平台 actual / 多 ABI | 解析 glTF；公开 encode |
| 改进错误 / 体积 | 静默空网格 |
| 上架三渠（新 changelog） | 无 ADR 换 C++ 内核 |

## 下游旁路

- 任意工程：`includeBuild` 本仓根（或已 `publishToMavenLocal`）
- Ohos：`includeBuild(…/ohosInclude)`（KBA 工具链）
