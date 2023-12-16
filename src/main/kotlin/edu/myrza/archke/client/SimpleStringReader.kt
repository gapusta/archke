package edu.myrza.archke.client

import edu.myrza.archke.client.SimpleStringReader.State.*

class SimpleStringReader {

    private var state = READ

    private var builder: StringBuilder = StringBuilder()

    fun read(chunk: ByteArray, length: Int) {
        for (idx in 0 until length) {
            val current = chunk[idx]

            if (state == READ) {
                if (current != SIMPLE_STR) throw IllegalStateException("+ was expected")
                state = READ_DATA
                continue
            }

            if (state == READ_DATA) {
                if (current == CR) continue
                if (current == LF) {
                    state = DONE
                    continue
                }
                builder.append(current.toInt().toChar())
            }
        }
    }

    fun state(): State = state

    fun payload(): String = builder.toString()

    fun done(): Boolean = state() == DONE

    enum class State {
        READ,
        READ_DATA,

        DONE
    }

    companion object {
        private const val SIMPLE_STR = 0x2b.toByte() // '+'
        private const val CR = 0x0d.toByte() // '\r'
        private const val LF = 0x0a.toByte() // '\n'
    }
}
