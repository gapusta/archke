package edu.myrza.archke.client

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SimpleStringReaderTests {

    @Test
    fun readOkTest() {
        val reader = SimpleStringReader()

        val ok = "+OK\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(ok, ok.size)

        assert(reader.done())
        assertEquals("OK", reader.payload())
    }

    @Test
    fun readTest() {
        val reader = SimpleStringReader()

        val ok = "+Catch me if you can, Mr. Holmes\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(ok, ok.size)

        assert(reader.done())
        assertEquals("Catch me if you can, Mr. Holmes", reader.payload())
    }

}
