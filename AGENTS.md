# kmp-draco · AI 协作路由

## 项目概况

- 产品：**kmp-draco**（Draco **bitstream** 解码原语；Kotlin 脸 + Rust `draco-core`）
- 身份：**ADR 0001**；与 Holm / Kuikly / glTF **解耦**；平行于 `kmp-wgpu` 独立仓故事
- Maven：`io.github.sofarnobug:kmp-draco`；包名 `com.jlj.kmp.draco`
- M0：仅 Android `arm64-v8a`；同步 `Draco.decode`；失败 `DracoDecodeException`
- 消费方（Holm）：`holm-gltf` 硬依赖 · fail-loud → [KuiklyHolm ADR 0017](../KuiklyHolm/docs/adr/0017-kmp-draco-under-holm-gltf.md)

## 接手阅读顺序

1. 本文件
2. [CONTEXT.md](CONTEXT.md)
3. [docs/adr/0001-independent-rust-bitstream-android.md](docs/adr/0001-independent-rust-bitstream-android.md)
4. [README.md](README.md) · [docs/guide.md](docs/guide.md)
5. 维护：[CONTRIBUTING.md](CONTRIBUTING.md)

## 硬规则（摘录）

1. **只解比特流** → 分属性数组；禁止做成 glTF 加载器或网格 SDK。
2. **公开 API 不暴露 encode**；内核保持 decoder-only。
3. 失败必须 **抛异常 / 上抛**，禁止静默空网格。
4. 通用 GPU / Present 走 **`kmp-wgpu`**，不要扩进本仓。
5. 改身份 / 内核选型先改 **CONTEXT + ADR**。

## 仓库地图

| 路径 | 用途 |
|---|---|
| `kmp-draco/` | Kotlin + jniLibs |
| `kmp-draco-native/` | Rust + 构建脚本 + testdata |
| `docs/` | guide + adr |
| `CHANGELOG.md` | 版本与渠道 |
