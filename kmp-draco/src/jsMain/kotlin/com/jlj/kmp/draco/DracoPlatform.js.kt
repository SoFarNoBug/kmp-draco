package com.jlj.kmp.draco

/**
 * @CreateDate: 2026/11/19
 * @Author: jlj
 * @Description: H5 → window.__kmpDraco（先加载 dist/wasm 胶水 + wasm）。
 */
internal actual object DracoPlatform {
    actual fun decode(bytes: ByteArray): DecodedMesh {
        if (bytes.isEmpty()) {
            throw DracoDecodeException("empty Draco buffer")
        }
        if (!isWasmReady()) {
            throw DracoDecodeException("kmp-draco wasm not loaded (window.__kmpDraco)")
        }
        return try {
            val raw = decodeJs(toUint8Array(bytes))
            mapMesh(raw)
        } catch (t: Throwable) {
            throw DracoDecodeException(t.message ?: "wasm decode failed")
        }
    }
}

private fun isWasmReady(): Boolean =
    js(
        "typeof window !== 'undefined' && !!window.__kmpDraco && typeof window.__kmpDraco.decode === 'function'",
    ) as Boolean

private fun toUint8Array(bytes: ByteArray): dynamic {
    val u8 = js("new Uint8Array(arguments[0])").unsafeCast<(Int) -> dynamic>()(bytes.size)
    for (i in bytes.indices) {
        u8[i] = bytes[i]
    }
    return u8
}

private fun decodeJs(u8: dynamic): dynamic {
    val fn = js("window.__kmpDraco.decode")
    return fn(u8)
}

private fun mapMesh(raw: dynamic): DecodedMesh {
    val numPoints = (raw.numPoints as Number).toInt()
    val idxArr = raw.indices
    val indexLen = (idxArr.length as Number).toInt()
    val indices = IntArray(indexLen) { i -> (idxArr[i] as Number).toInt() }
    val attrArr = raw.attributes
    val attrLen = (attrArr.length as Number).toInt()
    val attributes = Array(attrLen) { ai ->
        val a = attrArr[ai]
        val valuesJs = a.values
        val vLen = (valuesJs.length as Number).toInt()
        val values = FloatArray(vLen) { vi -> (valuesJs[vi] as Number).toFloat() }
        DecodedAttribute(
            uniqueId = (a.uniqueId as Number).toInt(),
            attrType = (a.attrType as Number).toInt(),
            numComponents = (a.numComponents as Number).toInt(),
            values = values,
        )
    }
    return DecodedMesh(
        numPoints = numPoints,
        indices = indices,
        attributes = attributes,
    )
}
