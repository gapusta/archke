package edu.myrza.archke.client

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

class BooleanReaderTests {

    @Test
    fun readTrue() {
        val reader = BooleanReader()

        val input = "#t\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(input, input.size)

        assert(reader.done())
        assert(reader.payload())
    }

    @Test
    fun readFalse() {
        val reader = BooleanReader()

        val input = "#f\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(input, input.size)

        assert(reader.done())
        assertFalse(reader.payload())
    }

    @Test
    fun readChunked1() {
        val reader = BooleanReader()

        val input1 = "#t".toByteArray(Charsets.US_ASCII)
        val input2 = "\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(input1, input1.size)
        reader.read(input2, input2.size)

        assert(reader.done())
        assert(reader.payload())
    }

    @Test
    fun readChunked2() {
        val reader = BooleanReader()

        val input1 = "#".toByteArray(Charsets.US_ASCII)
        val input2 = "t".toByteArray(Charsets.US_ASCII)
        val input3 = "\r".toByteArray(Charsets.US_ASCII)
        val input4 = "\n".toByteArray(Charsets.US_ASCII)

        reader.read(input1, input1.size)
        reader.read(input2, input2.size)
        reader.read(input3, input3.size)
        reader.read(input4, input4.size)

        assert(reader.done())
        assert(reader.payload())
    }

}
