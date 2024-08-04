package edu.myrza.archke.server.controller.parser

import edu.myrza.archke.server.controller.parser.Reader.State.*
import java.nio.ByteBuffer

class Reader {

    private var state = READ_ARRAY

    private var argl = 0 // argument length
    private var arg: ByteBuffer = EMPTY_ARG

    private var argc = 0
    private var argv: ArrayList<ByteArray> = EMPTY_ARRAY

    fun state(): State = state

    fun payload(): Array<ByteArray> = argv.toTypedArray()

    fun done(): Boolean = state() == DONE

    fun read(chunk: ByteArray, start: Int, limit: Int): Int { // start - inclusive, limit - exclusive
        for (idx in start until limit) {
            val byte = chunk[idx]

            when (state) {
                READ_ARRAY -> {
                    if (byte != ARRAY) throw IllegalStateException("* was expected")

                    state = READ_ARRAY_LENGTH
                    continue
                }
                READ_ARRAY_LENGTH -> {
                    val digit = byte - 0x30 // 0x30 == '0' in ascii

                    if (digit in 0..9) {
                        argc = argc * 10 + digit
                        continue
                    }

                    if (byte == CR) continue
                    if (byte == LF) {
                        argv = ArrayList(argc)
                        state = if (argc == 0) DONE else READ_BINARY
                    }
                }
                READ_BINARY -> {
                    if (byte != BINARY_STR) throw IllegalStateException("$ was expected")

                    state = READ_BINARY_LENGTH
                    continue
                }
                READ_BINARY_LENGTH -> {
                    val digit = byte - 0x30 // 0x30 == '0' in ascii

                    if (digit in 0..9) {
                        argl = argl * 10 + digit
                        continue
                    }

                    if (byte == CR) continue
                    if (byte == LF) {
                        if (argl == 0) {
                            handle(EMPTY_ARG.array())
                        } else {
                            arg = ByteBuffer.wrap(ByteArray(argl))
                            argl = 0
                            state = READ_BINARY_DATA
                        }
                    }
                }
                READ_BINARY_DATA -> {
                    if (arg.hasRemaining() && byte != LF) arg.put(byte)
                    if (!arg.hasRemaining()) handle(arg.array())
                }
                DONE -> return idx
            }
        }

        return start + limit
    }

    private fun handle(arg: ByteArray) {
        argv.add(arg)
        argc--
        state = if (argc == 0) DONE else READ_BINARY
    }

    enum class State {
        READ_ARRAY,
        READ_ARRAY_LENGTH,
        READ_BINARY,
        READ_BINARY_LENGTH,
        READ_BINARY_DATA,
        DONE
    }

    companion object {
        private const val ARRAY = 0x2a.toByte() // '*'
        private const val BINARY_STR = 0x24.toByte() // '$'
        private const val CR = 0x0d.toByte() // '\r'
        private const val LF = 0x0a.toByte() // '\n'

        private val EMPTY_ARRAY = arrayListOf<ByteArray>()
        private val EMPTY_ARG = ByteBuffer.allocate(0)
    }
}
