package edu.myrza.archke.client.reader.impl

import edu.myrza.archke.client.reader.Reader
import edu.myrza.archke.client.reader.impl.BinaryStringReader.State.*
import java.nio.ByteBuffer

class BinaryStringReader: Reader<ByteArray?> {

    private var state = START

    private var binary: ByteBuffer? = null
    private var length = 0

    override fun payload(): ByteArray? = binary?.array()

    override fun done(): Boolean = state == DONE

    override fun read(chunk: ByteArray, occupied: Int) {
        for (idx in 0 until occupied) {
            val byte = chunk[idx]

            when (state) {
                START -> {
                    state = when (byte) {
                        BINARY_STR -> READ_BINARY_LENGTH
                        NULL_STR -> READ_NULL
                        else -> throw IllegalStateException("$ or _ was expected")
                    }
                }
                READ_BINARY_LENGTH -> {
                    val number = byte - 0x30 // 0x30 == '0' in ascii

                    if (number in 0..9) {
                        length = length * 10 + number
                        continue
                    }

                    if (byte == CR) continue
                    if (byte == LF) {
                        binary = ByteBuffer.wrap(ByteArray(length))
                        state = READ_BINARY_DATA
                    }
                }
                READ_BINARY_DATA -> {
                    if (binary!!.hasRemaining() && byte != LF) binary!!.put(byte)
                    if (!binary!!.hasRemaining()) state = DONE
                }
                READ_NULL -> {
                    if (byte == CR) continue
                    if (byte == LF) state = DONE
                }
                DONE -> break
            }
        }
    }

    enum class State {
        START,
        READ_NULL,
        READ_BINARY_LENGTH,
        READ_BINARY_DATA,
        DONE
    }

    companion object {
        private const val BINARY_STR = 0x24.toByte() // '$'
        private const val NULL_STR = 0x5f.toByte() // '_'
        private const val CR = 0x0d.toByte() // '\r'
        private const val LF = 0x0a.toByte() // '\n'
    }

}
