# Changelog

## Unreleased

（空）

## 0.1.0 — 2026-11-18

首发 M0：Android arm64 Draco **bitstream** 同步解码；Kotlin 脸 + Rust `draco-core` 内核。

### 能力

- `Draco.decode(ByteArray) → DecodedMesh`；失败 `DracoDecodeException`
- 分属性输出：`POSITION`（必有）/ `NORMAL` / `TEX_COORD` / `COLOR` / `GENERIC` + indices
- `attributeByUniqueId` 对齐 `KHR_draco_mesh_compression` 映射
- Native：`libkmp_draco_native.so`（arm64-v8a，release ~621 KiB）
- 测试：`kmp-draco-native/testdata/tokyo_prim0.drc`（`cargo test`）+ `connectedDebugAndroidTest`

### 分发渠道

| 渠道 | 坐标 / 包 | 状态 |
|---|---|---|
| mavenLocal | `io.github.sofarnobug:kmp-draco:0.1.0` | **可用**（`./publish-maven.sh local`） |
| Maven Central | 同上 | **未发**（另单） |
| iOS / ohpm / npm native | — | **无**（M0 仅 Android） |

### 边界（故意不做）

- 不解 glTF/GLB；不 encode；无 point cloud；无 x86 `.so`
