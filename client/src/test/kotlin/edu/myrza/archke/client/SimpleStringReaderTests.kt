package edu.myrza.archke.client

import edu.myrza.archke.client.reader.SimpleStringReader
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class SimpleStringReaderTests {

    @Test
    fun readOkTest() {
        val ok = "+OK\r\n".toByteArray(Charsets.US_ASCII)
        val reader = SimpleStringReader(ByteArrayInputStream(ok))
        val result = reader.read()

        assertEquals("OK", result)
    }

    @Test
    fun readTest() {
        val ok = "+Catch me if you can, Mr. Holmes\r\n".toByteArray(Charsets.US_ASCII)
        val reader = SimpleStringReader(ByteArrayInputStream(ok))
        val result = reader.read()

        assertEquals("Catch me if you can, Mr. Holmes", result)
    }

}