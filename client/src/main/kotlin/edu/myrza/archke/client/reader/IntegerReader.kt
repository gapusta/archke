package edu.myrza.archke.client.reader

import edu.myrza.archke.client.reader.IntegerReader.State.*

class IntegerReader {

    private var state = READ_INTEGER
    private var value = 0

    fun read(chunk: ByteArray, length: Int) {
        for (idx in 0 until length) {
            val current = chunk[idx]

            if (state == READ_INTEGER) {
                state = when(current) {
                    INTEGER -> READ_INTEGER_VALUE
                    else -> throw IllegalStateException(": was expected")
                }
                continue
            }

            if (state == READ_INTEGER_VALUE) {
                val number = current - 0x30 // 0x30 == '0' in ascii

                if (number in 0..9) {
                    value = value * 10 + number
                    continue
                }

                if (current == CR) continue
                if (current == LF) state = DONE
            }
        }
    }

    fun payload() = value

    fun done() = state == DONE

    enum class State {
        READ_INTEGER,
        READ_INTEGER_VALUE,
        DONE
    }

    companion object {
        private const val INTEGER = 0x3a.toByte() // '$'
        private const val CR = 0x0d.toByte() // '\r'
        private const val LF = 0x0a.toByte() // '\n'
    }

}
