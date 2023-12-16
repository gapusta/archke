package edu.myrza.archke.client

import edu.myrza.archke.client.BinaryStringReader.State.*
import java.nio.ByteBuffer

class BinaryStringReader {

    private var state = READ_BINARY

    private var binary: ByteBuffer = ByteBuffer.allocate(0)

    private var currentLength = 0

    fun read(chunk: ByteArray, length: Int) {
        for (idx in 0 until length) {
            val current = chunk[idx]

            if (state == READ_BINARY) {
                if (current != BINARY_STR) throw IllegalStateException("$ was expected")
                state = READ_BINARY_LENGTH
                continue
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

    fun state(): State = state

    fun payload(): ByteArray = binary.array()

    fun done(): Boolean = state() == DONE

    enum class State {
        READ_BINARY,
        READ_BINARY_LENGTH,
        READ_BINARY_DATA,

        READ_LF,
        DONE
    }

    companion object {
        private const val BINARY_STR = 0x24.toByte() // '$'
        private const val CR = 0x0d.toByte() // '\r'
        private const val LF = 0x0a.toByte() // '\n'
    }

}
