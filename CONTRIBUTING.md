# 开发者文档 · kmp-draco

给**维护 / 共建本仓**的开发者。  
路人看根 [README.md](README.md)；接入工程看 [docs/guide.md](docs/guide.md)。

## 仓库布局

| 路径 | 职责 |
|---|---|
| `kmp-draco/` | KMP 薄脸：`Draco` / `DecodedMesh` / Android JNI actual + jniLibs |
| `kmp-draco-native/` | Rust crate `kmp_draco_native` + `draco-core` + `build-android.sh` + testdata |
| `docs/guide.md` | 接入手册 |
| `docs/adr/` | 决策记录 |
| `CONTEXT.md` | 领域语言 / 边界 SoT |

本仓是跨端 **Draco bitstream 原语**：不解 glTF、不做 Present、不暴露 encode。不要把 `holm-gltf` / 网格 Present 塞回本仓。

## 本地构建 Native

改 Rust 后 **必须**重跑脚本（Gradle **不会**自动编 Rust）：

```bash
cd kmp-draco-native
./build-android.sh
# → kmp-draco/src/androidMain/jniLibs/arm64-v8a/libkmp_draco_native.so
```

工具链：

- `rustup`（`rust-version` 见 crate `Cargo.toml`，当前 **1.88+**）
- `ANDROID_NDK_HOME`
- `cargo-ndk`（`aarch64-linux-android`）
- 目标：本波只出 **arm64-v8a**

## 测试

```bash
# Rust 单测（含 tokyo_prim0.drc）
cd kmp-draco-native && cargo test

# Android 仪器（需 arm64 设备；夹具从 testdata 进 assets）
./gradlew :kmp-draco:connectedDebugAndroidTest
```

## 发布

**0.1.0 渠道状态**见 [CHANGELOG.md](CHANGELOG.md)。

```bash
./publish-maven.sh local    # → ~/.m2 …:kmp-draco:0.1.0（跳过签名）
# Central（需凭证）：
CENTRAL_USERNAME=… CENTRAL_PASSWORD=… GPG_KEY_PASSWORD=… ./publish-maven.sh
```

坐标：`io.github.sofarnobug:kmp-draco`。  
插件：`com.vanniktech.maven.publish`；版本属性 `mavenVersion` / `MAVEN_VERSION`（默认 `0.1.0`）。

**Central 发版另单**（M0 先狗粮 mavenLocal + Holm `includeBuild`）。

## 边界（改代码前对照）

| 可以 | 不可以 |
|---|---|
| 扩属性类型 / 平台 actual | 解析 glTF/GLB |
| 多 ABI / iOS·Ohos·Wasm native | 公开 encode API |
| 改进错误信息 / 体积 | 输出交错 VBO 当唯一契约 |
| | 静默返回空网格代替抛异常 |
| | 把 Filament / Google C++ 内核偷塞进 M0 身份（须新 ADR） |

身份真源：[CONTEXT.md](CONTEXT.md) · [ADR 0001](docs/adr/0001-independent-rust-bitstream-android.md)。

## 与 Holm 联调

1. 旁路：Holm `settings` 见 `../kmp-draco` → 默认 `includeBuild`
2. 或 `./publish-maven.sh local` + Holm `kmp.draco.local=false`
3. Demo Tokyo / `holm-gltf` KHR_draco 路径验收

消费方决策：[KuiklyHolm ADR 0017](../KuiklyHolm/docs/adr/0017-kmp-draco-under-holm-gltf.md)。
