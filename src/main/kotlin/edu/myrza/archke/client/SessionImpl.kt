package edu.myrza.archke.client

import java.net.Socket

class SessionImpl internal constructor(private val socket: Socket) : Session {

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

        val reader = SimpleStringReader()
        val buffer = ByteArray(BUFFER_MAX_SIZE)
        while (true) {
            val read = inputStream.read(buffer)
            reader.read(buffer, read)
            if (reader.done()) break
        }

        return reader.payload()
    }

    override fun get(key: ByteArray): ByteArray {
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

        return reader.payload()
    }

    override fun close() {
        socket.close()
    }

    companion object {
        private const val BUFFER_MAX_SIZE = 1024
    }

}
