package edu.myrza.archke.server.controller.parser

import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

class ReaderTests {

    @Test
    fun testSingleChunkMessage() {
        val reader = Reader()

        // ^17\r\nThis is a library
        val chunk1 = "^17\r\n".toByteArray(Charsets.US_ASCII)
        val chunk2 = "This is a library".toByteArray(Charsets.UTF_8)
        val size = chunk1.size + chunk2.size

        val single = ByteBuffer.wrap(ByteArray(size)).let {
            it.put(chunk1)
            it.put(chunk2)
            it.array()
        }

        reader.read(single, single.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "This is a library")
    }

    @Test
    fun testChunkedMessageSmallerThan10() {
        val reader = Reader()

        // ^5\r\nHello
        val chunk1 = "^".toByteArray(Charsets.US_ASCII)
        val chunk2 = "5".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\r".toByteArray(Charsets.US_ASCII)
        val chunk4 = "\n".toByteArray(Charsets.US_ASCII)
        val chunk5 = "Hel".toByteArray(Charsets.UTF_8)
        val chunk6 = "lo".toByteArray(Charsets.UTF_8)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)
        reader.read(chunk5, chunk5.size)
        reader.read(chunk6, chunk6.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "Hello")
    }

    @Test
    fun testChunkedMessageBiggerThan10() {
        val reader = Reader()

        // ^17\r\nThis is a library
        val chunk1 = "^".toByteArray(Charsets.US_ASCII)
        val chunk2 = "17".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\r".toByteArray(Charsets.US_ASCII)
        val chunk4 = "\n".toByteArray(Charsets.US_ASCII)
        val chunk5 = "This is".toByteArray(Charsets.UTF_8)
        val chunk6 = " a library".toByteArray(Charsets.UTF_8)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)
        reader.read(chunk5, chunk5.size)
        reader.read(chunk6, chunk6.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "This is a library")
    }

    @Test
    fun testChunkedMessageLengthSplitToMultipleChunks() {
        val reader = Reader()

        // ^17\r\nThis is a library
        val chunk1 = "^".toByteArray(Charsets.US_ASCII)
        val chunk2 = "1".toByteArray(Charsets.US_ASCII)
        val chunk3 = "7".toByteArray(Charsets.US_ASCII)
        val chunk4 = "\r".toByteArray(Charsets.US_ASCII)
        val chunk5 = "\n".toByteArray(Charsets.US_ASCII)
        val chunk6 = "This is".toByteArray(Charsets.UTF_8)
        val chunk7 = " a library".toByteArray(Charsets.UTF_8)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)
        reader.read(chunk5, chunk5.size)
        reader.read(chunk6, chunk6.size)
        reader.read(chunk7, chunk7.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "This is a library")
    }

    @Test
    fun testChunkedMessageLengthSplitToMultipleChunks2() {
        val reader = Reader()

        // ^17\r\nThis is a library
        val chunk1 = "^".toByteArray(Charsets.US_ASCII)
        val chunk2 = "1".toByteArray(Charsets.US_ASCII)
        val chunk3 = "7\r".toByteArray(Charsets.US_ASCII)
        val chunk4 = "\n".toByteArray(Charsets.US_ASCII)
        val chunk5 = "This is".toByteArray(Charsets.UTF_8)
        val chunk6 = " a library".toByteArray(Charsets.UTF_8)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)
        reader.read(chunk5, chunk5.size)
        reader.read(chunk6, chunk6.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "This is a library")
    }

    @Test
    fun testChunkedMessageLengthSplitToMultipleChunks3() {
        val reader = Reader()

        // ^17\r\nThis is a library
        val chunk1 = "^1".toByteArray(Charsets.US_ASCII)
        val chunk2 = "7\r".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\n".toByteArray(Charsets.US_ASCII)
        val chunk4 = "This is".toByteArray(Charsets.UTF_8)
        val chunk5 = " a library".toByteArray(Charsets.UTF_8)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)
        reader.read(chunk5, chunk5.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "This is a library")
    }

    @Test
    fun testChunkedMessageDividersAreInSingleChunk() {
        val reader = Reader()

        // ^17\r\nThis is a library
        val chunk1 = "^1".toByteArray(Charsets.US_ASCII)
        val chunk2 = "7".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\r\n".toByteArray(Charsets.US_ASCII)
        val chunk4 = "This is".toByteArray(Charsets.UTF_8)
        val chunk5 = " a library".toByteArray(Charsets.UTF_8)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)
        reader.read(chunk5, chunk5.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "This is a library")
    }

    @Test
    fun testEmptyRequest() {
        val reader = Reader()

        val chunk = "^0\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk, chunk.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "")
    }

    @Test
    fun testChunkedEmptyRequest() {
        val reader = Reader()

        val chunk1 = "^".toByteArray(Charsets.US_ASCII)
        val chunk2 = "0".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\r".toByteArray(Charsets.US_ASCII)
        val chunk4 = "\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)
        reader.read(chunk4, chunk4.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "")
    }

    @Test
    fun testChunkedEmptyRequest2() {
        val reader = Reader()

        val chunk1 = "^0".toByteArray(Charsets.US_ASCII)
        val chunk2 = "\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "")
    }

    @Test
    fun testChunkedEmptyRequest3() {
        val reader = Reader()

        val chunk1 = "^0\r".toByteArray(Charsets.US_ASCII)
        val chunk2 = "\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "")
    }

    @Test
    fun testChunkedEmptyRequest4() {
        val reader = Reader()

        val chunk1 = "^".toByteArray(Charsets.US_ASCII)
        val chunk2 = "0\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "")
    }

    @Test
    fun testChunkedEmptyRequest5() {
        val reader = Reader()

        val chunk1 = "^".toByteArray(Charsets.US_ASCII)
        val chunk2 = "0".toByteArray(Charsets.US_ASCII)
        val chunk3 = "\r\n".toByteArray(Charsets.US_ASCII)

        reader.read(chunk1, chunk1.size)
        reader.read(chunk2, chunk2.size)
        reader.read(chunk3, chunk3.size)

        assertEquals(reader.done(), true)

        val result = String(reader.payload(), Charsets.UTF_8)

        assertEquals(result, "")
    }

}
