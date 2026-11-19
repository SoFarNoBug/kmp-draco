package com.jlj.kmp.draco

/**
 * @CreateDate: 2026/11/18
 * @Author: jlj
 * @Description: 一次成功 decode 的分属性结果（非交错）。
 */
class DecodedMesh(
    val numPoints: Int,
    val indices: IntArray,
    val attributes: Array<DecodedAttribute>,
) {
    /** GeometryAttributeType codes mirrored from Rust. */
    object AttrType {
        const val POSITION: Int = 0
        const val NORMAL: Int = 1
        const val COLOR: Int = 2
        const val TEX_COORD: Int = 3
        const val GENERIC: Int = 4
    }

    fun attributeByUniqueId(uniqueId: Int): DecodedAttribute? =
        attributes.firstOrNull { it.uniqueId == uniqueId }

    fun named(attrType: Int): DecodedAttribute? =
        attributes.firstOrNull { it.attrType == attrType }

    val positions: FloatArray
        get() {
            val a = named(AttrType.POSITION)
                ?: throw DracoDecodeException("DecodedMesh missing POSITION")
            return a.values
        }

    val normals: FloatArray
        get() = named(AttrType.NORMAL)?.values ?: FloatArray(0)

    /**
     * 首个 `AttrType.TEX_COORD` 的值（多套 UV 时顺序不保证）。
     * glTF/`KHR_draco` 请用 [attributeByUniqueId] 按 extension 映射取语义。
     */
    val texCoords: FloatArray
        get() = named(AttrType.TEX_COORD)?.values ?: FloatArray(0)

    val colors: FloatArray
        get() = named(AttrType.COLOR)?.values ?: FloatArray(0)
}

/**
 * @CreateDate: 2026/11/18
 * @Author: jlj
 * @Description: 单条 Draco 属性（uniqueId 对齐 KHR_draco attributes 映射）。
 */
class DecodedAttribute(
    val uniqueId: Int,
    val attrType: Int,
    val numComponents: Int,
    val values: FloatArray,
)
