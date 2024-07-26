package edu.myrza.archke.client

import edu.myrza.archke.client.reader.impl.SimpleStringReader
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SimpleStringReaderTests {

    @Test
    fun readTest1() {
        val ok = "+OK\r\n".toByteArray(Charsets.US_ASCII)
        val reader = SimpleStringReader()

        reader.read(ok, ok.size)

        assertEquals(true, reader.done())
        assertEquals("OK", reader.payload())
    }

    @Test
    fun readTest2() {
        val input = "+Catch me if you can, Mr. Holmes\r\n".toByteArray(Charsets.US_ASCII)
        val reader = SimpleStringReader()

        reader.read(input, input.size)

        assertEquals(true, reader.done())
        assertEquals("Catch me if you can, Mr. Holmes", reader.payload())
    }

    @Test
    fun readChunkedTest() {
        val input1 = "+Catch m".toByteArray(Charsets.US_ASCII)
        val input2 = "e if y".toByteArray(Charsets.US_ASCII)
        val input3 = "ou can, Mr. Holme".toByteArray(Charsets.US_ASCII)
        val input4 = "s\r".toByteArray(Charsets.US_ASCII)
        val input5 = "\n".toByteArray(Charsets.US_ASCII)

        val reader = SimpleStringReader()

        reader.read(input1, input1.size)
        reader.read(input2, input2.size)
        reader.read(input3, input3.size)
        reader.read(input4, input4.size)
        reader.read(input5, input5.size)

        assertEquals(true, reader.done())
        assertEquals("Catch me if you can, Mr. Holmes", reader.payload())
    }

}
