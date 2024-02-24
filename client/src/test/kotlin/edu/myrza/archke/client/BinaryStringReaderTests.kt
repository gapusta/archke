package edu.myrza.archke.client

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BinaryStringReaderTests {

    @Test
    fun readOkTest() {
        val reader = BinaryStringReader()

        val ok = "$2\r\nOK".toByteArray(Charsets.US_ASCII)

        reader.read(ok, ok.size)

        assert(reader.done())

        val response = String(reader.payload(), Charsets.US_ASCII)

        assertEquals("OK", response)
    }

    @Test
    fun readTest() {
        val reader = BinaryStringReader()

        val ok = "$31\r\nCatch me if you can, Mr. Holmes".toByteArray(Charsets.US_ASCII)

        reader.read(ok, ok.size)

        assert(reader.done())

        val response = String(reader.payload(), Charsets.US_ASCII)

        assertEquals("Catch me if you can, Mr. Holmes", response)
    }

}