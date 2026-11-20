package com.jlj.kmp.draco

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * @CreateDate: 2026/11/20
 * @Author: jlj
 * @Description: DecodedMesh 纯 Kotlin 查询 API（不碰 native decode）。
 */
class DecodedMeshTest {

    private fun mesh(vararg attrs: DecodedAttribute, numPoints: Int = 2): DecodedMesh =
        DecodedMesh(
            numPoints = numPoints,
            indices = intArrayOf(0, 1, 0),
            attributes = attrs.toList().toTypedArray(),
        )

    private fun attr(
        uniqueId: Int,
        attrType: Int,
        values: FloatArray,
        numComponents: Int = 3,
    ): DecodedAttribute =
        DecodedAttribute(
            uniqueId = uniqueId,
            attrType = attrType,
            numComponents = numComponents,
            values = values,
        )

    @Test
    fun attributeByUniqueId_findsAndMisses() {
        val pos = attr(2, DecodedMesh.AttrType.POSITION, floatArrayOf(1f, 2f, 3f, 4f, 5f, 6f))
        val nrm = attr(1, DecodedMesh.AttrType.NORMAL, floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f))
        val m = mesh(pos, nrm)
        assertSame(pos, m.attributeByUniqueId(2))
        assertSame(nrm, m.attributeByUniqueId(1))
        assertNull(m.attributeByUniqueId(99))
    }

    @Test
    fun named_returnsFirstOfType() {
        val uv0 = attr(4, DecodedMesh.AttrType.TEX_COORD, floatArrayOf(0f, 0f, 1f, 1f), numComponents = 2)
        val uv1 = attr(5, DecodedMesh.AttrType.TEX_COORD, floatArrayOf(0.5f, 0.5f, 0f, 1f), numComponents = 2)
        val m = mesh(uv0, uv1)
        assertSame(uv0, m.named(DecodedMesh.AttrType.TEX_COORD))
        assertNull(m.named(DecodedMesh.AttrType.POSITION))
    }

    @Test
    fun positions_returnsValues_orThrowsWhenMissing() {
        val pos = attr(2, DecodedMesh.AttrType.POSITION, floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f))
        assertContentEquals(pos.values, mesh(pos).positions)
        assertFailsWith<DracoDecodeException> {
            mesh(attr(1, DecodedMesh.AttrType.NORMAL, floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f))).positions
        }
    }

    @Test
    fun optionalAccessors_emptyWhenMissing_elseValues() {
        val empty = mesh()
        assertEquals(0, empty.normals.size)
        assertEquals(0, empty.texCoords.size)
        assertEquals(0, empty.colors.size)

        val nrm = attr(1, DecodedMesh.AttrType.NORMAL, floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f))
        val uv = attr(4, DecodedMesh.AttrType.TEX_COORD, floatArrayOf(0f, 1f, 1f, 0f), numComponents = 2)
        val col = attr(7, DecodedMesh.AttrType.COLOR, floatArrayOf(1f, 0f, 0f, 1f, 0f, 1f, 0f, 1f), numComponents = 4)
        val m = mesh(nrm, uv, col)
        assertContentEquals(nrm.values, m.normals)
        assertContentEquals(uv.values, m.texCoords)
        assertContentEquals(col.values, m.colors)
    }

    @Test
    fun attrType_constants_matchDracoGeometryCodes() {
        assertEquals(0, DecodedMesh.AttrType.POSITION)
        assertEquals(1, DecodedMesh.AttrType.NORMAL)
        assertEquals(2, DecodedMesh.AttrType.COLOR)
        assertEquals(3, DecodedMesh.AttrType.TEX_COORD)
        assertEquals(4, DecodedMesh.AttrType.GENERIC)
        assertTrue(mesh().indices.isNotEmpty())
    }
}
