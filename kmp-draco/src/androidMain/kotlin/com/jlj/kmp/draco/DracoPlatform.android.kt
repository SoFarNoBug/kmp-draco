package com.jlj.kmp.draco

/**
 * @CreateDate: 2026/11/18
 * @Author: jlj
 * @Description: Android JNI → libkmp_draco_native.so。
 */
internal actual object DracoPlatform {
    actual fun decode(bytes: ByteArray): DecodedMesh = AndroidDracoPlatform.decode(bytes)
}

internal object AndroidDracoPlatform {
    init {
        System.loadLibrary("kmp_draco_native")
    }

    fun decode(bytes: ByteArray): DecodedMesh {
        if (bytes.isEmpty()) {
            throw DracoDecodeException("empty Draco buffer")
        }
        return nativeDecode(bytes)
            ?: throw DracoDecodeException("nativeDecode returned null")
    }

    @JvmStatic
    private external fun nativeDecode(bytes: ByteArray): DecodedMesh?
}
