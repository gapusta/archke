package edu.myrza.archke.client

import edu.myrza.archke.client.reader.*
import edu.myrza.archke.client.reader.impl.BinaryStringReader
import edu.myrza.archke.client.reader.impl.BooleanReader
import edu.myrza.archke.client.reader.impl.IntegerReader
import edu.myrza.archke.client.reader.impl.SimpleStringReader
import java.io.BufferedOutputStream
import java.net.Socket

class ClientImpl internal constructor(private val socket: Socket) : Client {

    private val inputStream = socket.getInputStream()
    private val buffer = ByteArray(BUFFER_MAX_SIZE)

    private val outputStream = BufferedOutputStream(socket.getOutputStream(), BUFFER_MAX_SIZE)

    override fun set(key: ByteArray, value: ByteArray): String {
        write(SET, key, value)

        return readSimpleString()
    }

    override fun get(key: ByteArray): ByteArray? {
        write(GET, key)

        return readBinaryString()
    }

    override fun delete(key: ByteArray): Int {
        write(DEL, key)

        return readInteger()
    }

    override fun exists(key: ByteArray): Boolean {
        write(EXISTS, key)

        return readBoolean()
    }

    private fun readSimpleString() = readWith(SimpleStringReader())

    private fun readBinaryString() = readWith(BinaryStringReader())

    private fun readInteger() = readWith(IntegerReader())

    private fun readBoolean() = readWith(BooleanReader())

    private fun <T> readWith(reader: Reader<T>): T {
        while (!reader.done()) {
            val read = inputStream.read(buffer)
            reader.read(buffer, read)
        }

        return reader.payload()
    }

    private fun write(vararg array: ByteArray) {
        var header = "*${array.size}\r\n".toByteArray(Charsets.US_ASCII)

        outputStream.write(header)

        for (element in array) {
            header = "$${element.size}\r\n".toByteArray(Charsets.US_ASCII)

            outputStream.write(header)
            outputStream.write(element)
        }

        outputStream.flush()
    }

    override fun close() {
        socket.close()
    }

    companion object {
        private val SET = "SET".toByteArray(Charsets.US_ASCII)
        private val GET = "GET".toByteArray(Charsets.US_ASCII)
        private val DEL = "DEL".toByteArray(Charsets.US_ASCII)
        private val EXISTS = "EXISTS".toByteArray(Charsets.US_ASCII)

        private const val BUFFER_MAX_SIZE = 2 * 1048576 // 2 mb
    }

}
