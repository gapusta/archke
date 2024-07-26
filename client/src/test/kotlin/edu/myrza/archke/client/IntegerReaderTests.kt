package edu.myrza.archke.client

import edu.myrza.archke.client.reader.impl.IntegerReader
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IntegerReaderTests {

    @Test
    fun read1() {
        val reader = IntegerReader()
        val input = ":1\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(input, input.size)

        assert(reader.done())
        assertEquals(1, reader.payload())
    }

    @Test
    fun read0() {
        val reader = IntegerReader()
        val input = ":0\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(input, input.size)

        assert(reader.done())
        assertEquals(0, reader.payload())
    }

    @Test
    fun read69() {
        val reader = IntegerReader()
        val input = ":69\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(input, input.size)

        assert(reader.done())
        assertEquals(69, reader.payload())
    }

    @Test
    fun read420() {
        val reader = IntegerReader()
        val input = ":420\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(input, input.size)

        assert(reader.done())
        assertEquals(420, reader.payload())
    }

    @Test
    fun read1000() {
        val reader = IntegerReader()
        val input = ":1000\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(input, input.size)

        assert(reader.done())
        assertEquals(1000, reader.payload())
    }

    @Test
    fun read1000Chunked() {
        val reader = IntegerReader()

        val input1 = ":".toByteArray(Charsets.US_ASCII)
        val input2 = "1".toByteArray(Charsets.US_ASCII)
        val input3 = "0".toByteArray(Charsets.US_ASCII)
        val input4 = "0".toByteArray(Charsets.US_ASCII)
        val input5 = "0".toByteArray(Charsets.US_ASCII)
        val input6 = "\r".toByteArray(Charsets.US_ASCII)
        val input7 = "\n".toByteArray(Charsets.US_ASCII)

        reader.read(input1, input1.size)
        reader.read(input2, input2.size)
        reader.read(input3, input3.size)
        reader.read(input4, input4.size)
        reader.read(input5, input5.size)
        reader.read(input6, input6.size)
        reader.read(input7, input7.size)

        assert(reader.done())
        assertEquals(1000, reader.payload())
    }

}
