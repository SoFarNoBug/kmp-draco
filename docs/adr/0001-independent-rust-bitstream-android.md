# ADR 0001: 独立 Rust 原语仓 · 只解比特流 · M0 仅 Android

- Status: Accepted
- Date: 2026-11-18

## Context

Holm 最小 glTF（`holm-gltf`）遇到 `KHR_draco_mesh_compression`（如 LittlestTokyo）时无法展开 mesh，表现为空白。需要一条与 Holm / glTF 解耦的解码原语，形态对齐 `kmp-wgpu`（独立仓 + Kotlin 脸 + Rust 内核）。审讯（2026-11-18）在体积（偏官方 C++ 子集 wasm）与身份（Rust 原语仓）之间选择了身份。

## Decision

1. **独立仓**：`~/Studio/kmp/kmp-draco`；Maven `io.github.sofarnobug:kmp-draco`（M0 单 artifact）。
2. **边界**：只解 Draco **比特流 → 分属性数组 + 元数据**；**不懂** glTF/GLB/JSON；**不**做交错 VBO / 不透明 handle。
3. **内核**：Rust + crates.io **`draco-core`（decoder-only features）**；公开 API **不暴露 encode**。拒绝以 Google C++ 为 M0 内核（可逆但会改仓身份）；拒绝双内核 SPI。
4. **Kotlin API**：同步 `decode(bytes) → DecodedMesh`；失败抛 **`DracoDecodeException`**；调度由调用方负责。
5. **M0 范围**：仅 **Android** native；仅 **triangle mesh**；POSITION 必有，NORMAL / TEXCOORD_* / COLOR / indices **有则出**。iOS / Ohos / Wasm 后补。
6. **消费方约定（Holm）**：`holm-gltf` **硬依赖**本库；解析到 Draco bufferView 即调用；失败上抛，禁止再吞成空白。细节见 Holm ADR 0017。

## Considered Options

- Google Draco C++ / `draco_decoder_gltf`（体积公开更小）→ 拒绝作 M0 内核（与「Rust 原语仓」身份冲突）
- 双内核回落 → 拒绝（两边付账）
- `holm-gltf` 经 Provider/SPI 注入解码器 → 拒绝（本波选硬依赖，换简单）
- 输出交错缓冲或只保证 POSITION → 拒绝（绑定决策泄漏 / Tokyo 材质易错）

## Consequences

- 须新建 Kotlin/Android/JNI（或等价）+ Rust crate；体积用 Tokyo 等样本实测，不挡选型。
- `holm-gltf` 引入本坐标后，不用模型的宿主仍可不引 `holm-gltf`；引了则带上 Draco native。
- 非 Android 端在补齐前，含 `KHR_draco` 的资产在那些端仍应 fail-loud。

## Relates

- [KuiklyHolm ADR 0017](../../../KuiklyHolm/docs/adr/0017-kmp-draco-under-holm-gltf.md)（消费方硬依赖 / fail-loud）
- [KuiklyHolm ADR 0015](../../../KuiklyHolm/docs/adr/0015-kmp-wgpu-independent-repo.md) / 0013（`kmp-wgpu` 独立原语仓——平行故事）
- 本仓 [CONTEXT.md](../../CONTEXT.md) · [docs/guide.md](../guide.md)
