package edu.myrza.archke.server

import edu.myrza.archke.client.Command
import edu.myrza.archke.server.Processor.State.*
import edu.myrza.archke.server.consumer.MessageConsumer
import edu.myrza.archke.util.getBytes
import edu.myrza.archke.util.getPositiveInt
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class Processor private constructor (
    private var key: SelectionKey,
    private val channel: SocketChannel,
    private val consumer: MessageConsumer
) : Runnable {

    private var state = READING_HEADER

    private var command = 0
    private var length = 0

    private var input = ByteBuffer.allocate(HEADER_SIZE)
    private var output = ByteBuffer.allocate(HEADER_SIZE)

    private var outputPayload = ByteArray(0)

    override fun run() {
        when (state) {
            READING_HEADER, READING_PAYLOAD -> read()
            WRITING_HEADER, WRITING_PAYLOAD -> write()
        }
    }

    private fun read() {
        val read = channel.read(input) // scattering read

        if (read == -1) { // client signaled connection close
            channel.close()
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
            command = 0
            length = 0
            input = ByteBuffer.allocate(HEADER_SIZE)
        }
    }

    private fun process() {
        if (command == Command.PROCESS.code) outputPayload = consumer.consume(input.array())

        // prepare output header
        output.put(Response.OK.code.getBytes())
        output.put(outputPayload.size.getBytes())
        // flip output so write could write its data into channel
        output.flip()

        // write event registration
        key.interestOps(SelectionKey.OP_WRITE) // register writing event listening
        state = WRITING_HEADER
    }

    private fun write() {
        channel.write(output)

        if (state == WRITING_HEADER && !output.hasRemaining()) {
            // output header is written completely
            output = ByteBuffer.wrap(outputPayload)
            state = WRITING_PAYLOAD
        }

        if (state == WRITING_PAYLOAD && !output.hasRemaining()) {
            // output payload is written completely
            output = ByteBuffer.allocate(HEADER_SIZE)

            // register reading event listening
            key.interestOps(SelectionKey.OP_READ)
            state = READING_HEADER
        }
    }

    private enum class State { READING_HEADER, READING_PAYLOAD, WRITING_HEADER, WRITING_PAYLOAD }

    companion object {

        // 4 bytes - command
        // 4 bytes - payload length
        private const val HEADER_SIZE = 8

        fun create(channel: SocketChannel, selector: Selector, consumer: MessageConsumer): Processor {
            channel.configureBlocking(false)
            val key = channel.register(selector, SelectionKey.OP_READ)
            return Processor(key, channel, consumer)
        }

    }

}
