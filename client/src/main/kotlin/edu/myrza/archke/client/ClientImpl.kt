package edu.myrza.archke.client

import edu.myrza.archke.client.reader.BinaryStringReader
import edu.myrza.archke.client.reader.BooleanReader
import edu.myrza.archke.client.reader.IntegerReader
import edu.myrza.archke.client.reader.SimpleStringReader
import java.net.Socket

class ClientImpl internal constructor(private val socket: Socket) : Client {

    private val inputStream = socket.getInputStream()
    private val outputStream = socket.getOutputStream()

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

        val reader = SimpleStringReader(inputStream)
        val response = reader.read()

        return response
    }

    override fun get(key: ByteArray): ByteArray? {
        val header = "*2\r\n$3\r\nGET".toByteArray(Charsets.US_ASCII)
        val keyHeader = "$${key.size}\r\n".toByteArray(Charsets.US_ASCII)

        outputStream.write(header)
        outputStream.write(keyHeader)
        outputStream.write(key)
        outputStream.flush()

        val reader = BinaryStringReader()
        val buffer = ByteArray(BUFFER_MAX_SIZE)
        while (true) {
            val read = inputStream.read(buffer)
            reader.read(buffer, read)
            if (reader.done()) break
        }

        return if(!reader.isNull()) reader.payload() else null
    }

    override fun delete(key: ByteArray): Int {
        val header = "*2\r\n$3\r\nDEL".toByteArray(Charsets.US_ASCII)
        val keyHeader = "$${key.size}\r\n".toByteArray(Charsets.US_ASCII)

        outputStream.write(header)
        outputStream.write(keyHeader)
        outputStream.write(key)
        outputStream.flush()

        val reader = IntegerReader()
        val buffer = ByteArray(BUFFER_MAX_SIZE)
        while (true) {
            val read = inputStream.read(buffer)
            reader.read(buffer, read)
            if (reader.done()) break
        }

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
        val buffer = ByteArray(BUFFER_MAX_SIZE)
        while (true) {
            val read = inputStream.read(buffer)
            reader.read(buffer, read)
            if (reader.done()) break
        }

        return reader.payload()
    }

    override fun close() {
        socket.close()
    }

    companion object {
        private const val BUFFER_MAX_SIZE = 1024
    }

}
