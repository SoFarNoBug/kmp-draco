# `:kmp-draco`（Kotlin 模块）

Maven：`io.github.sofarnobug:kmp-draco`  
包名：`com.jlj.kmp.draco`

## 公开面

| 类型 | 职责 |
|---|---|
| `object Draco` | `decode(ByteArray): DecodedMesh` |
| `DecodedMesh` | 分属性结果 + `AttrType` + 查找辅助 |
| `DecodedAttribute` | `uniqueId` / `attrType` / `numComponents` / `values` |
| `DracoDecodeException` | 显式失败 |

`internal expect object DracoPlatform`：仅 **androidMain** 提供 actual（`System.loadLibrary("kmp_draco_native")` + JNI）。

## 源码布局

```text
src/
  commonMain/kotlin/com/jlj/kmp/draco/
  androidMain/kotlin/…/DracoPlatform.android.kt
  androidMain/jniLibs/arm64-v8a/libkmp_draco_native.so
  androidInstrumentedTest/…/DracoDecodeInstrumentedTest.kt
```

## 接入

见仓根 [docs/guide.md](../docs/guide.md)。改 `.so` 须先跑 [`../kmp-draco-native/build-android.sh`](../kmp-draco-native/build-android.sh)。
