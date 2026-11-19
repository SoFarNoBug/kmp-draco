package com.jlj.kmp.draco

/**
 * @CreateDate: 2026/11/18
 * @Author: jlj
 * @Description: 平台解码实现钩子（M0 仅 Android actual）。
 */
internal expect object DracoPlatform {
    fun decode(bytes: ByteArray): DecodedMesh
}
