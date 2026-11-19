# CONTEXT · kmp-draco

与 Holm / Kuikly / glTF **无关**的跨端 Draco **比特流**解码原语：Kotlin 脸 + Rust（`draco-core`）内核；只把压缩几何解成属性数组。

## Language

**kmp-draco**：
独立原语库产品名与 Maven artifact（`io.github.sofarnobug:kmp-draco`）；职责仅 Draco bitstream → 分属性缓冲。
_Avoid_：holm-draco；glTF/GLB 解析器；Filament；通用网格 SDK

**Bitstream / Draco bufferView 载荷**：
`KHR_draco_mesh_compression`（或裸 `.drc`）里那一段压缩字节；本库的唯一输入形态。
_Avoid_：整个 `.gltf` / `.glb` 文件；JSON 场景图

**DecodedMesh**：
一次成功 decode 的结果：分属性数组 + 元数据；非交错 VBO、非不透明 native handle。
_Avoid_：Interleaved buffer；GPU buffer；glTF Accessor

**MeshDecoder 路径**：
只承诺 **triangle mesh**；point cloud 非本库语言。
_Avoid_：把点云 / Gaussian 算进完成态

**DracoDecodeException**：
解码失败时的显式失败信号。
_Avoid_：返回 null / 空网格并只打 log

**C ABI / wasm bridge**：
iOS·Ohos 用手写 C ABI；H5 用 wasm-bindgen；Android 保留 JNI。
_Avoid_：强制 Android 改走 C；把 Present/Session 塞进本库

## SoT

| 主题 | 路径 |
| --- | --- |
| 身份（M0） | [`docs/adr/0001-independent-rust-bitstream-android.md`](docs/adr/0001-independent-rust-bitstream-android.md) |
| 三端 decode（M1） | [`docs/adr/0002-ios-ohos-wasm-decode.md`](docs/adr/0002-ios-ohos-wasm-decode.md) |
| 路人 / 接入 | [`README.md`](README.md) · [`docs/guide.md`](docs/guide.md) |
| 维护 / 发版 | [`CONTRIBUTING.md`](CONTRIBUTING.md) · [`CHANGELOG.md`](CHANGELOG.md) |
| AI 路由 | [`AGENTS.md`](AGENTS.md) |
| 消费方（Holm） | [KuiklyHolm ADR 0017](../KuiklyHolm/docs/adr/0017-kmp-draco-under-holm-gltf.md) |

## 当前状态

- M0 Android + M1 iOS/Ohos/Wasm decode 源码与本地 `dist/` 已落地（ADR 0002）；三渠上架未做
- Tokyo fixture：`cargo test`（含 C ABI）绿；Wasm Node smoke points=104；Android 仪器 / Holm Demo 真机已通
- Holm：`includeBuild` / `kmp.draco.local`；Ohos 用 `ohosInclude/`
