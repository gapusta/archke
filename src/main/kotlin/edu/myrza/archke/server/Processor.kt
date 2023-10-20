package edu.myrza.archke.server

import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class Processor(
    private val selector: Selector,
    private var selectionKey: SelectionKey,
    private val socketChannel: SocketChannel
) : Runnable {

    private var state = State.READING_HEADER

    private val inputHeader = ByteBuffer.allocate(3)
    private var command: Byte = 0
    private var length: Short = 0
    private val input = ByteBuffer.allocate(65534) // 64 kb

    private val outputHeader = ByteBuffer.allocate(3)
    private val output = ByteBuffer.allocate(65534) // 64 kb

    override fun run() {
        when (state) {
            State.READING_HEADER,
            State.READING_PAYLOAD -> read()
            State.WRITING -> write()
        }
    }

    private fun read() {
        if (state == State.READING_HEADER) {
            socketChannel.read(inputHeader)
            if (inputHeader.position() == HEADER_SIZE) {
                // header is completely read
                inputHeader.flip()
                command = inputHeader.get()
                length = inputHeader.getShort()
                state = State.READING_PAYLOAD
            }
        }
        else if (state == State.READING_PAYLOAD) {
            socketChannel.read(input)
            if (input.position() == length.toInt()) {
                // payload is read completely
                input.flip()

                process()

                // clean up
                inputHeader.clear()
                input.clear()

                // event registration
                selectionKey.cancel() // cancel listening to read event
                selectionKey = socketChannel.register(selector, SelectionKey.OP_WRITE) // register listening to writing event
                state = State.WRITING
            }
        }
    }

    private fun process() {
        // TODO : process

        // TODO : prepare output

        // flip output so write could write its data into channel
        output.flip()
    }

    private fun write() {
        if (outputHeader.hasRemaining() || output.hasRemaining()) {
            socketChannel.write(arrayOf(outputHeader, output))
        } else {
            outputHeader.clear()
            state = State.READING_HEADER

            selectionKey.cancel()
            selectionKey = socketChannel.register(selector, SelectionKey.OP_READ)
        }
    }

    private enum class State {
         READING_HEADER, READING_PAYLOAD, WRITING
    }

    companion object {

        const val HEADER_SIZE = 3

        fun create(socketChannel: SocketChannel, selector: Selector): Processor {
            socketChannel.configureBlocking(false)
            val key = socketChannel.register(selector, SelectionKey.OP_READ)
            return Processor(selector, key, socketChannel)
        }

    }

}
