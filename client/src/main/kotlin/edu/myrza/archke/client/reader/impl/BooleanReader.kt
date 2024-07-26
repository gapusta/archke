package edu.myrza.archke.client.reader.impl

import edu.myrza.archke.client.reader.Reader
import edu.myrza.archke.client.reader.impl.BooleanReader.State.*

class BooleanReader : Reader<Boolean> {

    private var state = READ
    private var payload = false

    fun state(): State = state

    override fun payload(): Boolean = payload

    override fun done(): Boolean = state() == DONE

    override fun read(chunk: ByteArray, occupied: Int) {
        for (idx in 0 until occupied) {
            val current = chunk[idx]

            if (state == READ) {
                if (current != BOOLEAN) throw IllegalStateException("+ was expected")
                state = READ_DATA
                continue
            }

            if (state == READ_DATA) {
                if (current == TRUE) payload = true
                if (current == FALSE) payload = false
                if (current == CR) continue
                if (current == LF) state = DONE
            }
        }
    }

    enum class State {
        READ,
        READ_DATA,
        DONE
    }

    companion object {
        private const val BOOLEAN = 0x23.toByte() // '#'
        private const val TRUE = 0x74.toByte() // 't'
        private const val FALSE = 0x66.toByte() // 'f'
        private const val CR = 0x0d.toByte() // '\r'
        private const val LF = 0x0a.toByte() // '\n'
    }

}
