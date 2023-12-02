package edu.myrza.archke.server.controller.parser

import edu.myrza.archke.server.controller.parser.Reader.State.*
import java.nio.ByteBuffer

class Reader {

    private var state = READ_COMMAND

    private var payload = ByteBuffer.allocate(0)
    private var payloadLength = 0

    fun read(chunk: ByteArray, length: Int) {
        var current = 0

        if (state == READ_COMMAND && length > 0) {
            // command has been read, right now we only have single command (PRINT)
            state = READ_LENGTH
            if (length > 1) current = 1 else return
        }

        if (state == READ_LENGTH) {
            while (current < length) {
                val number = chunk[current] - 0x30 // 0x30 == '0' in ascii

                if (number in 0..9) {
                    payloadLength = payloadLength * 10 + number
                    current++
                    continue
                }

                // length has been read
                state = READ_CR
                break
            }
        }

        if (state == READ_CR) {
            if (current >= length) return // CR has not been received yet
            if (chunk[current++] != CR) throw IllegalArgumentException("CR was expected")
            state = READ_LF
        }

        if (state == READ_LF) {
            if (current >= length) return // LF has not been received yet
            if (chunk[current++] != LF) throw IllegalArgumentException("LF was expected")

            payload = ByteBuffer.allocate(payloadLength)
            state = READ_PAYLOAD
        }

        if (state == READ_PAYLOAD) {
            if (current < length) for (idx in current until length) payload.put(chunk[idx])
            if (!payload.hasRemaining() || payloadLength == 0) state = DONE
        }
    }

    fun state(): State = state

    fun payload(): ByteArray = payload.array()

    fun done(): Boolean = state() == DONE

    enum class State {
        READ_COMMAND,
        READ_LENGTH,
        READ_CR,
        READ_LF,
        READ_PAYLOAD,
        DONE
    }

    companion object {
        private const val CR = 0x0d.toByte()
        private const val LF = 0x0a.toByte()
    }
}
