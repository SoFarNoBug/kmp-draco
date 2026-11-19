# `:kmp-draco`（Kotlin 模块）

Maven：`io.github.sofarnobug:kmp-draco`  
包名：`com.jlj.kmp.draco`

## 公开面

| 类型 | 职责 |
|---|---|
| `object Draco` | `decode(ByteArray): DecodedMesh` |
| `DecodedMesh` / `DecodedAttribute` | 分属性结果 |
| `DracoDecodeException` | 显式失败 |

## 源码集

| Source set | 桥 |
|---|---|
| `androidMain` | JNI + jniLibs |
| `iosMain` + `nativeMain` | cinterop C ABI |
| `ohosArm64Main`（`build.ohos.gradle.kts`） | 同上 |
| `jsMain` | `window.__kmpDraco` |

## 接入

见仓根 [docs/guide.md](../docs/guide.md)。改 native 须重跑 [`../kmp-draco-native/build-*.sh`](../kmp-draco-native/)。
