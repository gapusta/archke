package edu.myrza.archke.client

import edu.myrza.archke.client.reader.*
import edu.myrza.archke.client.reader.impl.BinaryStringReader
import edu.myrza.archke.client.reader.impl.BooleanReader
import edu.myrza.archke.client.reader.impl.IntegerReader
import edu.myrza.archke.client.reader.impl.SimpleStringReader
import java.net.Socket

class ClientImpl internal constructor(private val socket: Socket) : Client {

    private val inputStream = socket.getInputStream()
    private val outputStream = socket.getOutputStream()
    private val buffer = ByteArray(BUFFER_MAX_SIZE)

    override fun set(key: ByteArray, value: ByteArray): String {
        val header = "*3\r\n$3\r\nSET".toByteArray(Charsets.US_ASCII)
        val keyHeader = "$${key.size}\r\n".toByteArray(Charsets.US_ASCII)
        val valueHeader = "$${value.size}\r\n".toByteArray(Charsets.US_ASCII)

        outputStream.write(header)
        outputStream.write(keyHeader)
        outputStream.write(key)
        outputStream.write(valueHeader)
        outputStream.write(value)
        outputStream.flush()

        val reader = SimpleStringReader()

        read(reader)

        return reader.payload()
    }

    override fun get(key: ByteArray): ByteArray? {
        val header = "*2\r\n$3\r\nGET".toByteArray(Charsets.US_ASCII)
        val keyHeader = "$${key.size}\r\n".toByteArray(Charsets.US_ASCII)

        outputStream.write(header)
        outputStream.write(keyHeader)
        outputStream.write(key)
        outputStream.flush()

        val reader = BinaryStringReader()

        read(reader)

        return reader.payload()
    }

    override fun delete(key: ByteArray): Int {
        val header = "*2\r\n$3\r\nDEL".toByteArray(Charsets.US_ASCII)
        val keyHeader = "$${key.size}\r\n".toByteArray(Charsets.US_ASCII)

        outputStream.write(header)
        outputStream.write(keyHeader)
        outputStream.write(key)
        outputStream.flush()

        val reader = IntegerReader()

        read(reader)

        return reader.payload()
    }

    override fun exists(key: ByteArray): Boolean {
        val header = "*2\r\n$6\r\nEXISTS".toByteArray(Charsets.US_ASCII)
        val keyHeader = "$${key.size}\r\n".toByteArray(Charsets.US_ASCII)

        outputStream.write(header)
        outputStream.write(keyHeader)
        outputStream.write(key)
        outputStream.flush()

        val reader = BooleanReader()

        read(reader)

        return reader.payload()
    }

    private fun read(reader: Reader) {
        while (!reader.done()) {
            val read = inputStream.read(buffer)
            reader.read(buffer, read)
        }
    }

    override fun close() {
        socket.close()
    }

    companion object {
        private const val BUFFER_MAX_SIZE = 1024
    }

}
