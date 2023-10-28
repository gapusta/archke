package edu.myrza.archke.server

import edu.myrza.archke.server.Processor.State.*
import edu.myrza.archke.server.consumer.MessageConsumer
import edu.myrza.archke.util.getPositiveInt
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class Processor private constructor (
    private var selectionKey: SelectionKey,
    private val socketChannel: SocketChannel,
    private val consumer: MessageConsumer
) : Runnable {

    private var state = READING_HEADER

    private var command = 0
    private var length = 0

    private var input = ByteBuffer.allocate(HEADER_SIZE)
    private val output = ByteBuffer.allocate(HEADER_SIZE)

    override fun run() {
        when (state) {
            READING_HEADER, READING_PAYLOAD -> read()
            WRITING_HEADER, WRITING_PAYLOAD -> write()
        }
    }

    private fun read() {
        val read = socketChannel.read(input) // scattering read

        if (read == -1) { // client signaled connection close
            socketChannel.close()
            println("INFO : Connection closed")
            return
        }

        if (state == READING_HEADER && !input.hasRemaining()) {
            // header is completely read
            command = input.getPositiveInt(0)
            length = input.getPositiveInt(4)
            input = ByteBuffer.allocate(length)
            state = READING_PAYLOAD
        }

        if (state == READING_PAYLOAD && !input.hasRemaining()) {
            // payload is completely read
            process()

            input = ByteBuffer.allocate(HEADER_SIZE)

            // write event registration
            selectionKey.interestOps(SelectionKey.OP_WRITE) // register writing event listening
            state = WRITING_HEADER
        }
    }

    private fun process() {
        if (length <= 0) return

        // process
        consumer.consume(input.array())

        // prepare output
        output.putInt(OK_RESPONSE_CODE)
        output.putInt(EMPTY_PAYLOAD_LENGTH)

        // flip output so write could write its data into channel
        output.flip()
    }

    private fun write() {
        socketChannel.write(output)

        if (state == WRITING_HEADER && !output.hasRemaining()) {
            // output is written completely
            output.clear()

            selectionKey.interestOps(SelectionKey.OP_READ) // register reading event listening
            state = READING_HEADER
        }
    }

    private enum class State { READING_HEADER, READING_PAYLOAD, WRITING_HEADER, WRITING_PAYLOAD }

    companion object {

        // 4 bytes - command
        // 4 bytes - payload length
        private const val HEADER_SIZE = 8

        private const val OK_RESPONSE_CODE = 1
        private const val EMPTY_PAYLOAD_LENGTH = 0

        fun create(channel: SocketChannel, selector: Selector, consumer: MessageConsumer): Processor {
            channel.configureBlocking(false)
            val key = channel.register(selector, SelectionKey.OP_READ)
            return Processor(key, channel, consumer)
        }

    }

}
