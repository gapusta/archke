package edu.myrza.archke.server

import edu.myrza.archke.server.Processor.State.*
import edu.myrza.archke.server.consumer.MessageConsumer
import edu.myrza.archke.util.getPositiveInt
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class Processor private constructor (
    private val selector: Selector,
    private var selectionKey: SelectionKey,
    private val socketChannel: SocketChannel,
    private val consumer: MessageConsumer
) : Runnable {

    private var state = READING_HEADER

    private var command = 0
    private var length = 0

    private val inputHeader = ByteBuffer.allocate(HEADER_SIZE)
    private val inputPayload = ByteBuffer.allocate(PAYLOAD_MAX_SIZE)
    private val input = arrayOf(inputHeader, inputPayload)

    private val outputHeader = ByteBuffer.allocate(HEADER_SIZE)

    override fun run() {
        when (state) {
            READING_HEADER, READING_PAYLOAD -> read()
            WRITING_HEADER, WRITING_PAYLOAD -> write()
        }
    }

    private fun read() {
        socketChannel.read(input) // scattering read

        if (state == READING_HEADER && !inputHeader.hasRemaining()) {
            // header is completely read
            command = inputHeader.getPositiveInt(0)
            length = inputHeader.getPositiveInt(4)
            state = READING_PAYLOAD
        }

        if (state == READING_PAYLOAD && inputPayload.position() == length) {
            // payload is completely read
            process()

            inputHeader.clear()
            inputPayload.clear()

            // write event registration
            selectionKey.cancel() // cancel read event listening
            selectionKey = socketChannel.register(selector, SelectionKey.OP_WRITE) // register writing event listening
            state = WRITING_HEADER
        }
    }

    private fun process() {
        if (length <= 0) return

        // process
        consumer.consume(inputPayload.array())

        // prepare output
        outputHeader.putInt(OK_RESPONSE_CODE)
        outputHeader.putInt(EMPTY_PAYLOAD_LENGTH)

        // flip output so write could write its data into channel
        outputHeader.flip()
    }

    private fun write() {
        socketChannel.write(outputHeader)

        if (state == WRITING_HEADER && !outputHeader.hasRemaining()) {
            // output is written completely
            outputHeader.clear()

            selectionKey.cancel() // unregister writing
            selectionKey = socketChannel.register(selector, SelectionKey.OP_READ) // register reading

            state = READING_HEADER
        }
    }

    private enum class State { READING_HEADER, READING_PAYLOAD, WRITING_HEADER, WRITING_PAYLOAD }

    companion object {

        // 4 bytes - command
        // 4 bytes - payload length
        private const val HEADER_SIZE = 8
        private const val PAYLOAD_MAX_SIZE = 2097152 // 2mb

        private const val OK_RESPONSE_CODE = 1
        private const val EMPTY_PAYLOAD_LENGTH = 0

        fun create(channel: SocketChannel, selector: Selector, consumer: MessageConsumer): Processor {
            channel.configureBlocking(false)
            val key = channel.register(selector, SelectionKey.OP_READ)
            return Processor(selector, key, channel, consumer)
        }

    }

}
