package com.jlj.kmp.draco

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @CreateDate: 2026/11/18
 * @Author: jlj
 * @Description: arm64 真机/模拟器：JNI decode Tokyo 切片冒烟（关 P1-plan-soft-drift）。
 */
@RunWith(AndroidJUnit4::class)
class DracoDecodeInstrumentedTest {
    @Test
    fun tokyoPrim0_decode_hasPositions() {
        val ctx = InstrumentationRegistry.getInstrumentation().context
        val bytes = ctx.assets.open("tokyo_prim0.drc").use { it.readBytes() }
        assertTrue("fixture empty", bytes.isNotEmpty())
        val mesh = try {
            Draco.decode(bytes)
        } catch (t: Throwable) {
            fail("Draco.decode threw: ${t.javaClass.simpleName}: ${t.message}")
            return
        }
        assertTrue("numPoints", mesh.numPoints > 0)
        assertTrue("indices", mesh.indices.isNotEmpty())
        val pos = mesh.attributeByUniqueId(2) ?: mesh.named(DecodedMesh.AttrType.POSITION)
        requireNotNull(pos) { "missing POSITION" }
        assertTrue("positions", pos.values.size >= mesh.numPoints * 3)
    }

    @Test(expected = DracoDecodeException::class)
    fun empty_throws() {
        Draco.decode(ByteArray(0))
    }
}
