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
一次成功 decode 的结果：分属性数组 + 元数据（分量数、组件类型等）；非交错 VBO、非不透明 native handle。
_Avoid_：Interleaved buffer；GPU buffer；glTF Accessor

**MeshDecoder 路径**：
M0 只承诺 **triangle mesh**；point cloud 非本波语言。
_Avoid_：把点云 / Gaussian 算进 M0 完成态

**DracoDecodeException**：
解码失败时的显式失败信号（空缓冲、非 mesh、不支持的流、无 POSITION 等）。
_Avoid_：返回 null / 空网格并只打 log

## SoT

| 主题 | 路径 |
| --- | --- |
| 身份与边界 | [`docs/adr/0001-independent-rust-bitstream-android.md`](docs/adr/0001-independent-rust-bitstream-android.md) |
| 路人 / 接入 | [`README.md`](README.md) · [`docs/guide.md`](docs/guide.md) |
| 维护 / 发版 | [`CONTRIBUTING.md`](CONTRIBUTING.md) · [`CHANGELOG.md`](CHANGELOG.md) |
| AI 路由 | [`AGENTS.md`](AGENTS.md) |
| 消费方（Holm） | [KuiklyHolm ADR 0017](../KuiklyHolm/docs/adr/0017-kmp-draco-under-holm-gltf.md) |

## 当前状态

- 审讯已钉（2026-11-18）：独立仓、bitstream-only、`draco-core`、M0 仅 Android、分属性输出、同步 API、抛异常
- M0 源码 + Android `.so` 已落地；Tokyo `testdata` Rust 单测 + androidInstrumentedTest 夹具；`publish-maven.sh local` 可用；Central 另单
- Holm：旁路仓在则默认 includeBuild；`kmp.draco.local=false` 走 Maven（ADR 0017）；Tokyo Demo 真机已通
- 文档：对齐 `kmp-wgpu` 的 README / guide / CONTRIBUTING / CHANGELOG + Holm 式 CONTEXT / ADR / AGENTS（2026-11-19）
