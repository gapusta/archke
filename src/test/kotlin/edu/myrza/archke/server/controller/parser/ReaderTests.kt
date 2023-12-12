package edu.myrza.archke.server.controller.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ReaderTests {

    @Test
    fun testRequest() {
        val reader = Reader()

        val singlePayload = "*3\r\n$3\r\nSET$5\r\nMYKEY$7\r\nMYVALUE".toByteArray(Charsets.US_ASCII)

        reader.read(singlePayload, singlePayload.size)

        assertEquals(reader.done(), true)

        val result = reader.payload()

        assertEquals(result.size, 3)
        assertEquals(String(result[0], Charsets.US_ASCII), "SET")
        assertEquals(String(result[1], Charsets.US_ASCII), "MYKEY")
        assertEquals(String(result[2], Charsets.US_ASCII), "MYVALUE")
    }

    @Test
    fun testChunkedRequest() {
        val reader = Reader()

        // [*][1][\r][\n][$3][\r\n][S][ET]
        val chunk1 = "*".toByteArray(Charsets.US_ASCII)
        val chunk2 = "1".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\r".toByteArray(Charsets.US_ASCII)
        val chunk4 = "\n".toByteArray(Charsets.US_ASCII)
        val chunk5 = "$3".toByteArray(Charsets.UTF_8)
        val chunk6 = "\r\n".toByteArray(Charsets.UTF_8)
        val chunk7 = "S".toByteArray(Charsets.UTF_8)
        val chunk8 = "ET".toByteArray(Charsets.UTF_8)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)
        reader.read(chunk5, chunk5.size)
        reader.read(chunk6, chunk6.size)
        reader.read(chunk7, chunk7.size)
        reader.read(chunk8, chunk8.size)

        assertEquals(reader.done(), true)

        val result = reader.payload()

        assertEquals(result.size, 1)
        assertEquals(String(result[0], Charsets.US_ASCII), "SET")
    }


    @Test
    fun testChunkedMessageLengthSplitToMultipleChunks1() {
        val reader = Reader()

        // [*][1][\r][\n][$1][1][\r\n][DIST][INGUISH]
        val chunk1 = "*".toByteArray(Charsets.US_ASCII)
        val chunk2 = "1".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\r".toByteArray(Charsets.US_ASCII)
        val chunk4 = "\n".toByteArray(Charsets.US_ASCII)
        val chunk5 = "$1".toByteArray(Charsets.UTF_8)
        val chunk6 = "1".toByteArray(Charsets.UTF_8)
        val chunk7 = "\r\n".toByteArray(Charsets.UTF_8)
        val chunk8 = "DIST".toByteArray(Charsets.UTF_8)
        val chunk9 = "INGUISH".toByteArray(Charsets.UTF_8)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)
        reader.read(chunk5, chunk5.size)
        reader.read(chunk6, chunk6.size)
        reader.read(chunk7, chunk7.size)
        reader.read(chunk8, chunk8.size)
        reader.read(chunk9, chunk9.size)

        assertEquals(reader.done(), true)

        val result = reader.payload()

        assertEquals(result.size, 1)
        assertEquals(String(result[0], Charsets.US_ASCII), "DISTINGUISH")
    }

    @Test
    fun testChunkedMessageLengthSplitToMultipleChunks2() {
        val reader = Reader()

        // [*][1][\r][\n][$1][1\r\n][DIST][INGUISH]
        val chunk1 = "*".toByteArray(Charsets.US_ASCII)
        val chunk2 = "1".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\r".toByteArray(Charsets.US_ASCII)
        val chunk4 = "\n".toByteArray(Charsets.US_ASCII)
        val chunk5 = "$1".toByteArray(Charsets.UTF_8)
        val chunk6 = "1\r\n".toByteArray(Charsets.UTF_8)
        val chunk7 = "DIST".toByteArray(Charsets.UTF_8)
        val chunk8 = "INGUISH".toByteArray(Charsets.UTF_8)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)
        reader.read(chunk5, chunk5.size)
        reader.read(chunk6, chunk6.size)
        reader.read(chunk7, chunk7.size)
        reader.read(chunk8, chunk8.size)

        assertEquals(reader.done(), true)

        val result = reader.payload()

        assertEquals(result.size, 1)
        assertEquals(String(result[0], Charsets.US_ASCII), "DISTINGUISH")
    }

    @Test
    fun testEmptyRequest() {
        val reader = Reader()

        val chunk = "*0\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk, chunk.size)

        assertEquals(true, reader.done())
        assertEquals(reader.payload().size, 0)
    }

    @Test
    fun testChunkedEmptyRequest() {
        val reader = Reader()

        val chunk1 = "*".toByteArray(Charsets.US_ASCII)
        val chunk2 = "0".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\r".toByteArray(Charsets.US_ASCII)
        val chunk4 = "\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)

        assertEquals(reader.done(), true)
        assertEquals(reader.payload().size, 0)
    }

    @Test
    fun testChunkedEmptyRequest2() {
        val reader = Reader()

        val chunk1 = "*0".toByteArray(Charsets.US_ASCII)
        val chunk2 = "\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)

        assertEquals(reader.done(), true)
        assertEquals(reader.payload().size, 0)
    }

    @Test
    fun testChunkedEmptyRequest3() {
        val reader = Reader()

        val chunk1 = "*0\r".toByteArray(Charsets.US_ASCII)
        val chunk2 = "\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)

        assertEquals(reader.done(), true)
        assertEquals(reader.payload().size, 0)
    }

    @Test
    fun testChunkedEmptyRequest4() {
        val reader = Reader()

        val chunk1 = "*".toByteArray(Charsets.US_ASCII)
        val chunk2 = "0\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)

        assertEquals(reader.done(), true)
        assertEquals(reader.payload().size, 0)
    }

    @Test
    fun testChunkedEmptyRequest5() {
        val reader = Reader()

        val chunk1 = "*".toByteArray(Charsets.US_ASCII)
        val chunk2 = "0".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)

        assertEquals(reader.done(), true)
        assertEquals(reader.payload().size, 0)
    }

    @Test
    fun testRequestArrayHasEmptyElement() {
        val reader = Reader()

        val chunk = "*1\r\n$0\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk, chunk.size)

        assertEquals(reader.done(), true)
        assertEquals(reader.payload().size, 1)
        assertEquals(reader.payload()[0].size, 0)
    }

    @Test
    fun testRequestArrayHasEmptyElements() {
        val reader = Reader()

        val chunk = "*3\r\n$0\r\n$3\r\nhey$0\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk, chunk.size)

        assertEquals(reader.done(), true)
        assertEquals(reader.payload().size, 3)

        assertEquals(reader.payload()[0].size, 0)
        assertEquals(reader.payload()[2].size, 0)

        assertEquals(String(reader.payload()[1], Charsets.US_ASCII), "hey")
    }

}
