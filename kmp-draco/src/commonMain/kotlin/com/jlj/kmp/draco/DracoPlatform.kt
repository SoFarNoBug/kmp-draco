package com.jlj.kmp.draco

/**
 * @CreateDate: 2026/11/18
 * @Author: jlj
 * @Description: 平台解码实现钩子（Android JNI；iOS/Ohos C ABI；JS wasm）。
 */
internal expect object DracoPlatform {
    fun decode(bytes: ByteArray): DecodedMesh
}
