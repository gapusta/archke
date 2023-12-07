package edu.myrza.archke.server.controller.parser

import edu.myrza.archke.server.controller.parser.Reader.State.*
import java.nio.ByteBuffer

class Reader {

    private var state = READ_COMMAND

    private var payload = ByteBuffer.allocate(0)
    private var payloadLength = 0

    fun read(chunk: ByteArray, length: Int) {
        for (current in 0 until length) {
            if (state == READ_COMMAND) {
                state = READ_LENGTH
                continue
            }

            if (state == READ_LENGTH) {
                val number = chunk[current] - 0x30 // 0x30 == '0' in ascii

                if (number in 0..9) {
                    payloadLength = payloadLength * 10 + number
                } else {
                    if (chunk[current] != CR) throw IllegalArgumentException("CR was expected")
                    state = READ_LF
                }
                continue
            }

            if (state == READ_LF) {
                if (chunk[current] != LF) throw IllegalArgumentException("LF was expected")
                if (payloadLength > 0) {
                    payload = ByteBuffer.allocate(payloadLength)
                    state = READ_PAYLOAD
                    continue
                } else {
                    state = DONE
                    break
                }
            }

            if (state == READ_PAYLOAD) {
                payload.put(chunk[current])
                if (!payload.hasRemaining()) state = DONE
            }
        }
    }

    fun state(): State = state

    fun payload(): ByteArray = payload.array()

    fun done(): Boolean = state() == DONE

    enum class State {
        READ_COMMAND,
        READ_LENGTH,
        READ_LF,
        READ_PAYLOAD,
        DONE
    }

    companion object {
        private const val CR = 0x0d.toByte()
        private const val LF = 0x0a.toByte()
    }
}
