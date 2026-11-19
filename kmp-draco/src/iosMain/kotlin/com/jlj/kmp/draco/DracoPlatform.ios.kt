package com.jlj.kmp.draco

/**
 * @CreateDate: 2026/11/19
 * @Author: jlj
 * @Description: iOS cinterop → libkmp_draco_native（App 须链 KmpDracoNative.xcframework）。
 */
internal actual object DracoPlatform {
    actual fun decode(bytes: ByteArray): DecodedMesh = decodeViaCAbi(bytes)
}
