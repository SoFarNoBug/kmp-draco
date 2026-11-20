# `:kmp-draco`（KMP 原语模块）

跨端 Draco **bitstream** 解码（KMP 公共 API）。  
路人说明见根 [README.md](../README.md)；接入见 [docs/guide.md](../docs/guide.md)；构建发版见 [开发者文档](../CONTRIBUTING.md)。

## 设计要点

- **输入**：压缩几何比特流（`KHR_draco` bufferView 或裸 `.drc`）
- **输出**：分属性 `FloatArray` + indices（非交错、非 GPU buffer）
- **失败**：`DracoDecodeException`；禁止静默空网格
- **调度**：`Draco.decode` 同步；线程由调用方负责
- **链接**：Kotlin 脸不替你链 native；App 按端带产物

## 公开面

| 类型 | 职责 |
|---|---|
| `object Draco` | `decode(ByteArray): DecodedMesh` |
| `DecodedMesh` / `DecodedAttribute` | 分属性结果与查询 |
| `DracoDecodeException` | 显式失败 |

## 源码集

| Source set | 桥 |
|---|---|
| `androidMain` | JNI + jniLibs |
| `iosMain` + `nativeMain` | cinterop C ABI |
| `ohosArm64Main`（`build.ohos.gradle.kts`） | 同上 |
| `jsMain` | `window.__kmpDraco` |

Native 产物与改 Rust 后的脚本 → [`../kmp-draco-native`](../kmp-draco-native/)。API 全表与避坑 → [docs/guide.md](../docs/guide.md)。
