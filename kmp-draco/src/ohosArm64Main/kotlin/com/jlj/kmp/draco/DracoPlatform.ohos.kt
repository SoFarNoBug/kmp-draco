package com.jlj.kmp.draco

/**
 * @CreateDate: 2026/11/19
 * @Author: jlj
 * @Description: Ohos cinterop → libkmp_draco_native.so（App 须放入 libs/arm64-v8a）。
 */
internal actual object DracoPlatform {
    actual fun decode(bytes: ByteArray): DecodedMesh = decodeViaCAbi(bytes)
}
