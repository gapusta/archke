package edu.myrza.archke.util

import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteUtilsTest {

    @Test
    fun testConvert() {
        val buffer = ByteBuffer.allocate(4).apply { this.putInt(69) }

        val result = buffer.toPositiveInt(0)

        assertEquals(69, result)
    }

    @Test
    fun testConvertMinValue() {
        val buffer = ByteBuffer.allocate(4).apply { this.putInt(0) }

        val result = buffer.toPositiveInt(0)

        assertEquals(0, result)
    }

    @Test
    fun testConvertMaxValue() {
        val buffer = ByteBuffer.allocate(4).apply { this.putInt(2097152) }

        val result = buffer.toPositiveInt(0)

        assertEquals(2097152, result)
    }

    @Test
    fun testConvertWithOffset() {
        val buffer = ByteBuffer.allocate(5).apply {
            this.put(69)
            this.putInt(3142)
        }

        val result = buffer.toPositiveInt(1)

        assertEquals(3142, result)
    }

    @Test
    fun testConvertMinValueWithOffset() {
        val buffer = ByteBuffer.allocate(5).apply {
            this.put(69)
            this.putInt(0)
        }

        val result = buffer.toPositiveInt(1)

        assertEquals(0, result)
    }

    @Test
    fun testConvertMaxValueWithOffset() {
        val buffer = ByteBuffer.allocate(5).apply {
            this.put(69)
            this.putInt(2097152)
        }

        val result = buffer.toPositiveInt(1)

        assertEquals(2097152, result)
    }

}
