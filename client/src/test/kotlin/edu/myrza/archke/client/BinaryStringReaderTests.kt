package edu.myrza.archke.client

import edu.myrza.archke.client.reader.impl.BinaryStringReader
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BinaryStringReaderTests {

    @Test
    fun readOkTest() {
        val reader = BinaryStringReader()

        val ok = "$2\r\nOK".toByteArray(Charsets.US_ASCII)

        reader.read(ok, ok.size)

        val payload = reader.payload()

        assert(reader.done())
        assertNotNull(payload)

        val response = String(payload, Charsets.US_ASCII)

        assertEquals("OK", response)
    }

    @Test
    fun readTest() {
        val reader = BinaryStringReader()

        val ok = "$31\r\nCatch me if you can, Mr. Holmes".toByteArray(Charsets.US_ASCII)

        reader.read(ok, ok.size)

        val payload = reader.payload()

        assert(reader.done())
        assertNotNull(payload)

        val response = String(payload, Charsets.US_ASCII)

        assertEquals("Catch me if you can, Mr. Holmes", response)
    }

    @Test
    fun readChunkedTest() {
        val reader = BinaryStringReader()

        val chunk1 = "$31\r\nCatch me ".toByteArray(Charsets.US_ASCII)
        val chunk2 = "if you ".toByteArray(Charsets.US_ASCII)
        val chunk3 = "can, ".toByteArray(Charsets.US_ASCII)
        val chunk4 = "Mr. Holmes".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)

        val payload = reader.payload()

        assert(reader.done())
        assertNotNull(payload)

        val response = String(payload, Charsets.US_ASCII)

        assertEquals("Catch me if you can, Mr. Holmes", response)
    }

    @Test
    fun readChunkedTest2() {
        val reader = BinaryStringReader()

        val chunk1 = "$31".toByteArray(Charsets.US_ASCII)
        val chunk2 = "\r".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\n".toByteArray(Charsets.US_ASCII)
        val chunk4 = "Catch me if you ".toByteArray(Charsets.US_ASCII)
        val chunk5 = "can, ".toByteArray(Charsets.US_ASCII)
        val chunk6 = "Mr. Holmes".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)
        reader.read(chunk5, chunk5.size)
        reader.read(chunk6, chunk6.size)

        val payload = reader.payload()

        assert(reader.done())
        assertNotNull(payload)

        val response = String(payload, Charsets.US_ASCII)

        assertEquals("Catch me if you can, Mr. Holmes", response)
    }

    @Test
    fun readChunkedTest3() {
        val reader = BinaryStringReader()

        val chunk1 = "$31\r".toByteArray(Charsets.US_ASCII)
        val chunk2 = "\nCatch me if you ".toByteArray(Charsets.US_ASCII)
        val chunk3 = "can, ".toByteArray(Charsets.US_ASCII)
        val chunk4 = "Mr. Holmes".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)

        val payload = reader.payload()

        assert(reader.done())
        assertNotNull(payload)

        val response = String(payload, Charsets.US_ASCII)

        assertEquals("Catch me if you can, Mr. Holmes", response)
    }

    @Test
    fun readNullTest() {
        val reader = BinaryStringReader()

        val data = "_\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(data, data.size)

        assert(reader.done())
        assertNull(reader.payload())
    }

}
