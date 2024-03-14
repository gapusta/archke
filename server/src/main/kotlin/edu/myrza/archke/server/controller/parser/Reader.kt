package edu.myrza.archke.server.controller.parser

import edu.myrza.archke.server.controller.parser.Reader.State.*
import java.nio.ByteBuffer

class Reader {

    private var state = READ_ARRAY
    private var arrayLength = 0

    private var binary: ByteBuffer = ByteBuffer.allocate(0)
    private var currentLength = 0

    private lateinit var array: ArrayList<ByteArray>

    fun read(chunk: ByteArray, length: Int) {
        for (idx in 0 until length) {
            val current = chunk[idx]

            if (state == READ_ARRAY) {
                if (current != ARRAY) throw IllegalStateException("* was expected")
                state = READ_ARRAY_LENGTH
                continue
            }

            if (state == READ_BINARY) {
                if (current != BINARY_STR) throw IllegalStateException("$ was expected")
                state = READ_BINARY_LENGTH
                continue
            }

            if (state == READ_ARRAY_LENGTH || state == READ_BINARY_LENGTH) {
                val number = current - 0x30 // 0x30 == '0' in ascii

                if (number in 0..9) {
                    currentLength = currentLength * 10 + number
                    continue
                }

                if (current == CR) continue

                if (current == LF && state == READ_ARRAY_LENGTH) {
                    arrayLength = currentLength
                    array = ArrayList(arrayLength)
                    currentLength = 0
                    state = if (arrayLength == 0) DONE else READ_BINARY
                }

                if (current == LF && state == READ_BINARY_LENGTH) {
                    binary = ByteBuffer.wrap(ByteArray(currentLength))
                    currentLength = 0
                    state = READ_BINARY_DATA
                }
            }

            if (state == READ_BINARY_DATA && binary.hasRemaining() && current != LF) {
                binary.put(current)
            }

            if (state == READ_BINARY_DATA && !binary.hasRemaining()) {
                array.add(binary.array())
                arrayLength--
                state = if (arrayLength == 0) DONE else READ_BINARY
            }
        }
    }

    fun state(): State = state

    fun payload(): Array<ByteArray> = array.toTypedArray()

    fun done(): Boolean = state() == DONE

    enum class State {
        READ_ARRAY,
        READ_ARRAY_LENGTH,
        READ_BINARY,
        READ_BINARY_LENGTH,
        READ_BINARY_DATA,

        READ_LF,
        DONE
    }

    companion object {
        private const val ARRAY = 0x2a.toByte() // '*'
        private const val BINARY_STR = 0x24.toByte() // '$'
        private const val CR = 0x0d.toByte() // '\r'
        private const val LF = 0x0a.toByte() // '\n'
    }
}
