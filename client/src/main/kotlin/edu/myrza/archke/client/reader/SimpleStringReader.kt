package edu.myrza.archke.client.reader

import edu.myrza.archke.client.reader.SimpleStringReader.State.*
import java.io.InputStream

class SimpleStringReader(private val input: InputStream) {

    private var state = READ

    private val builder = StringBuffer()
    private val buffer = ByteArray(BUFFER_SIZE)

    fun read(): String {
        while (true) {
            val read = input.read(buffer)

            read(buffer, read)

            if (state == DONE) break
        }

        return builder.toString().also { builder.setLength(0) }
    }

    private fun read(chunk: ByteArray, length: Int) {
        for (idx in 0 until length) {
            val byte = chunk[idx]

            when (state) {
                READ -> {
                    if (byte != SIMPLE_STR) throw IllegalStateException("+ was expected")

                    state = READ_DATA
                }
                READ_DATA -> {
                    if (byte == CR) continue
                    if (byte == LF) {
                        state = DONE
                        continue
                    }
                    builder.append(byte.toInt().toChar())
                }
                DONE -> break
            }
        }
    }

    private enum class State {
        READ,
        READ_DATA,
        DONE
    }

    companion object {
        private const val SIMPLE_STR = 0x2b.toByte() // '+'
        private const val CR = 0x0d.toByte() // '\r'
        private const val LF = 0x0a.toByte() // '\n'

        private const val BUFFER_SIZE = 8 * 1024 // 8kb
    }
}
