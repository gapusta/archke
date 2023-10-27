package edu.myrza.archke.util

import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteUtilsTest {

    @Test
    fun testBytesToInt() {
        val buffer = ByteBuffer.allocate(4).apply { this.putInt(69) }

        val result = buffer.getPositiveInt(0)

        assertEquals(69, result)
    }

    @Test
    fun testBytesToIntMinValue() {
        val buffer = ByteBuffer.allocate(4).apply { this.putInt(0) }

        val result = buffer.getPositiveInt(0)

        assertEquals(0, result)
    }

    @Test
    fun testBytesToIntMaxValue() {
        val buffer = ByteBuffer.allocate(4).apply { this.putInt(2097152) }

        val result = buffer.getPositiveInt(0)

        assertEquals(2097152, result)
    }

    @Test
    fun testBytesToIntWithOffset() {
        val buffer = ByteBuffer.allocate(5).apply {
            this.put(69)
            this.putInt(3142)
        }

        val result = buffer.getPositiveInt(1)

        assertEquals(3142, result)
    }

    @Test
    fun testBytesToIntMinValueWithOffset() {
        val buffer = ByteBuffer.allocate(5).apply {
            this.put(69)
            this.putInt(0)
        }

        val result = buffer.getPositiveInt(1)

        assertEquals(0, result)
    }

    @Test
    fun testBytesToIntMaxValueWithOffset() {
        val buffer = ByteBuffer.allocate(5).apply {
            this.put(69)
            this.putInt(2097152)
        }

        val result = buffer.getPositiveInt(1)

        assertEquals(2097152, result)
    }

    @Test
    fun testIntToBytes() {
        val expected = byteArrayOf(0, 0, 16, 115)
        val result = 4211.getBytes()

        assertEquals(expected[0], result[0])
        assertEquals(expected[1], result[1])
        assertEquals(expected[2], result[2])
        assertEquals(expected[3], result[3])
    }

    @Test
    fun testIntToBytesMinValue() {
        val expected = byteArrayOf(0, 0, 0, 0)
        val result = 0.getBytes()

        assertEquals(expected[0], result[0])
        assertEquals(expected[1], result[1])
        assertEquals(expected[2], result[2])
        assertEquals(expected[3], result[3])
    }

    @Test
    fun testIntToBytesMaxValue() {
        val expected = byteArrayOf(0, 32, 0, 0)
        val result = 2097152.getBytes()

        assertEquals(expected[0], result[0])
        assertEquals(expected[1], result[1])
        assertEquals(expected[2], result[2])
        assertEquals(expected[3], result[3])
    }

}
