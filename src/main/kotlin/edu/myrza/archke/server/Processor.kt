package edu.myrza.archke.server

import edu.myrza.archke.util.toPositiveInt
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class Processor(
    private val selector: Selector,
    private var selectionKey: SelectionKey,
    private val socketChannel: SocketChannel
) : Runnable {

    private var state = State.READING

    private var command = 0
    private var length = 0

    private val input = ByteBuffer.allocate(BUFFER_MAX_SIZE) // 2 mb
    private val output = ByteBuffer.allocate(BUFFER_MAX_SIZE) // 2 mb

    override fun run() {
        when (state) {
            State.READING -> read()
            State.WRITING -> write()
        }
    }

    private fun read() {
        socketChannel.read(input)

        if (input.position() == HEADER_SIZE) {
            // header is completely read
            command = input.toPositiveInt(0)
            length = input.toPositiveInt(4)
            input.mark()
            return
        }

        if (input.position() == length + HEADER_SIZE) {
            // payload is completely read
            process()

            input.clear()

            // write event registration
            selectionKey.cancel() // cancel listening to read event
            selectionKey = socketChannel.register(selector, SelectionKey.OP_WRITE) // register listening to writing event
            state = State.WRITING
        }
    }

    private fun process() {
        // process
        val message = String(input.array(), HEADER_SIZE, HEADER_SIZE + length, Charsets.UTF_8)

        // prepare output
        output.putInt(OK_RESPONSE_CODE)
        output.putInt(EMPTY_PAYLOAD_LENGTH)

        // flip output so write could write its data into channel
        output.flip()
    }

    private fun write() {
        socketChannel.write(output)
        if (!output.hasRemaining()) {
            // output is written completely
            output.clear()

            selectionKey.cancel() // unregister writing
            selectionKey = socketChannel.register(selector, SelectionKey.OP_READ) // register reading

            state = State.READING
        }
    }

    private enum class State {
         READING, WRITING
    }

    companion object {

        // 4 bytes - command
        // 4 bytes - payload length
        private const val HEADER_SIZE = 8
        private const val BUFFER_MAX_SIZE = 2097152

        private const val OK_RESPONSE_CODE = 1
        private const val EMPTY_PAYLOAD_LENGTH = 0

        fun create(socketChannel: SocketChannel, selector: Selector): Processor {
            socketChannel.configureBlocking(false)
            val key = socketChannel.register(selector, SelectionKey.OP_READ)
            return Processor(selector, key, socketChannel)
        }

    }

}
