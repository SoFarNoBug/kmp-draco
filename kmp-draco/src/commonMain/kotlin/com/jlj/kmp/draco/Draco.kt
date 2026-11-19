package com.jlj.kmp.draco

/**
 * @CreateDate: 2026/11/18
 * @Author: jlj
 * @Description: 公开入口：Draco 比特流 → [DecodedMesh]（ADR 0001）。
 */
object Draco {
    /**
     * 同步解码。失败抛 [DracoDecodeException]。调度由调用方负责。
     */
    fun decode(bytes: ByteArray): DecodedMesh = DracoPlatform.decode(bytes)
}
