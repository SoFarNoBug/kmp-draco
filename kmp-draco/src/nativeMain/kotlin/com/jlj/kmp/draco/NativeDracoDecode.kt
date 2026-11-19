package com.jlj.kmp.draco

import kmp.draco.native.cinterop.kmp_draco_attr_num_components
import kmp.draco.native.cinterop.kmp_draco_attr_type
import kmp.draco.native.cinterop.kmp_draco_attr_unique_id
import kmp.draco.native.cinterop.kmp_draco_attr_value_count
import kmp.draco.native.cinterop.kmp_draco_attr_values
import kmp.draco.native.cinterop.kmp_draco_decode
import kmp.draco.native.cinterop.kmp_draco_last_error
import kmp.draco.native.cinterop.kmp_draco_mesh_attr_count
import kmp.draco.native.cinterop.kmp_draco_mesh_free
import kmp.draco.native.cinterop.kmp_draco_mesh_index_count
import kmp.draco.native.cinterop.kmp_draco_mesh_indices
import kmp.draco.native.cinterop.kmp_draco_mesh_num_points
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned

/**
 * @CreateDate: 2026/11/19
 * @Author: jlj
 * @Description: iOS / Ohos：C ABI → [DecodedMesh]（拷贝后 free）。
 */
@OptIn(ExperimentalForeignApi::class)
internal fun decodeViaCAbi(bytes: ByteArray): DecodedMesh {
    if (bytes.isEmpty()) {
        throw DracoDecodeException("empty Draco buffer")
    }
    val meshPtr = bytes.usePinned { pinned ->
        kmp_draco_decode(
            pinned.addressOf(0).reinterpret(),
            bytes.size.toULong(),
        )
    }
    if (meshPtr == null) {
        throw DracoDecodeException(readLastError())
    }
    try {
        val numPoints = kmp_draco_mesh_num_points(meshPtr)
        val indexCount = kmp_draco_mesh_index_count(meshPtr).toInt()
        val indices = IntArray(indexCount)
        val idxPtr: CPointer<IntVar>? = kmp_draco_mesh_indices(meshPtr)
        if (indexCount > 0 && idxPtr != null) {
            for (i in 0 until indexCount) {
                indices[i] = idxPtr[i]
            }
        }
        val attrCount = kmp_draco_mesh_attr_count(meshPtr).toInt()
        val attributes = Array(attrCount) { ai ->
            val uid = kmp_draco_attr_unique_id(meshPtr, ai.toUInt())
            val at = kmp_draco_attr_type(meshPtr, ai.toUInt())
            val comps = kmp_draco_attr_num_components(meshPtr, ai.toUInt())
            val valueCount = kmp_draco_attr_value_count(meshPtr, ai.toUInt()).toInt()
            val values = FloatArray(valueCount)
            val valPtr: CPointer<FloatVar>? = kmp_draco_attr_values(meshPtr, ai.toUInt())
            if (valueCount > 0 && valPtr != null) {
                for (i in 0 until valueCount) {
                    values[i] = valPtr[i]
                }
            }
            DecodedAttribute(
                uniqueId = uid,
                attrType = at,
                numComponents = comps,
                values = values,
            )
        }
        return DecodedMesh(
            numPoints = numPoints,
            indices = indices,
            attributes = attributes,
        )
    } finally {
        kmp_draco_mesh_free(meshPtr)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun readLastError(): String = memScoped {
    val buf = allocArray<ByteVar>(512)
    val n = kmp_draco_last_error(buf, 512u)
    if (n == 0u) {
        return@memScoped "kmp_draco_decode failed"
    }
    // Copy until NUL
    val bytes = ByteArray(n.toInt()) { i -> buf[i].toByte() }
    bytes.decodeToString()
}
