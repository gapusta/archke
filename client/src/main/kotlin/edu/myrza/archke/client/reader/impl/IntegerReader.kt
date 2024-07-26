package edu.myrza.archke.client.reader.impl

import edu.myrza.archke.client.reader.Reader
import edu.myrza.archke.client.reader.impl.IntegerReader.State.*

class IntegerReader : Reader<Int> {

    private var state = READ_INTEGER
    private var value = 0

    override fun payload() = value

    override fun done() = state == DONE

    override fun read(chunk: ByteArray, occupied: Int) {
        for (idx in 0 until occupied) {
            val current = chunk[idx]

            when (state) {
                READ_INTEGER -> {
                    if (current != INTEGER) throw IllegalStateException(": was expected")
                    state = READ_INTEGER_VALUE
                }
                READ_INTEGER_VALUE -> {
                    val number = current - 0x30 // 0x30 == '0' in ascii

                    if (number in 0..9) {
                        value = value * 10 + number
                        continue
                    }

                    if (current == CR) continue
                    if (current == LF) state = DONE
                }
                DONE -> break
            }
        }
    }

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
