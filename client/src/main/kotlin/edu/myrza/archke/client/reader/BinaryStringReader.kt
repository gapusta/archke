package edu.myrza.archke.client.reader

import edu.myrza.archke.client.reader.BinaryStringReader.State.*
import java.nio.ByteBuffer

class BinaryStringReader {

    private var state = READ_BINARY

    private var binary: ByteBuffer = ByteBuffer.allocate(0)

    private var currentLength = 0

    fun read(chunk: ByteArray, length: Int) {
        for (idx in 0 until length) {
            val current = chunk[idx]

            if (state == READ_BINARY) {
                state = when(current) {
                    BINARY_STR -> READ_BINARY_LENGTH
                    NULL_STR -> READ_NULL
                    else -> throw IllegalStateException("$ or _ was expected")
                }
                continue
            }

            if (state == READ_NULL) {
                if (current == CR) continue
                if (current == LF) state = DONE_NULL
            }

            if (state == READ_BINARY_LENGTH) {
                val number = current - 0x30 // 0x30 == '0' in ascii

                if (number in 0..9) {
                    currentLength = currentLength * 10 + number
                    continue
                }

                if (current == CR) continue

                if (current == LF) {
                    binary = ByteBuffer.wrap(ByteArray(currentLength))
                    currentLength = 0
                    state = READ_BINARY_DATA
                }
            }

            if (state == READ_BINARY_DATA && binary.hasRemaining() && current != LF) {
                binary.put(current)
            }

            if (state == READ_BINARY_DATA && !binary.hasRemaining()) {
                state = DONE
            }
        }
    }

    fun payload(): ByteArray = binary.array()

    fun done(): Boolean = state == DONE || state == DONE_NULL

    fun isNull(): Boolean = state == DONE_NULL

    enum class State {
        READ_BINARY,
        READ_NULL,
        READ_BINARY_LENGTH,
        READ_BINARY_DATA,

        READ_LF,
        DONE,
        DONE_NULL
    }

    companion object {
        private const val BINARY_STR = 0x24.toByte() // '$'
        private const val NULL_STR = 0x5f.toByte() // '_'
        private const val CR = 0x0d.toByte() // '\r'
        private const val LF = 0x0a.toByte() // '\n'
    }

}
