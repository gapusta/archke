package edu.myrza.archke.server.io

import edu.myrza.archke.client.Command
import edu.myrza.archke.server.io.Handler.State.*
import edu.myrza.archke.server.controller.Controller
import edu.myrza.archke.server.controller.DefaultController
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class Handler private constructor (
    private var key: SelectionKey,
    private val channel: SocketChannel,
    private val controller: Controller
) : Runnable {

    private var state = READING_COMMAND

    private var command = 0
    private var length = 0
    private var current = 1 // current processed byte

    private var request = ByteBuffer.allocate(HEADER_MAX_SIZE)
    private var response = ByteBuffer.allocate(0)

    override fun run() {
        when (state) {
            READING_COMMAND,
            READING_LENGTH,
            READING_CR,
            READING_LF,
            READING_PAYLOAD -> read()
            WRITING_RESPONSE -> write()
        }
    }

    private fun read() {
        val read = channel.read(request)

        if (read == -1) { // client signaled connection close
            channel.close()
            println("INFO : Connection closed")
            return
        }

        if (state == READING_COMMAND && request.position() > 0) {
            // command has been read
            command = Command.PROCESS.code
            state = READING_LENGTH
        }

        if (state == READING_LENGTH) {
            while (current < request.position()) {
                val number = request[current] - 0x30 // 0x30 == '0' in ascii

                if (number in 0..9) {
                    length = length * 10 + number
                    current++
                    continue
                }

                // length has been read
                state = READING_CR
                break
            }
        }

        if (state == READING_CR) {
            if (request.position() == current) return // CR has not been received yet
            if (request[current] != CR) {
                processError("CR was expected at position $current")
                return
            }
            current++
            state = READING_LF
        }

        if (state == READING_LF) {
            if (request.position() == current) return // LF has not been received yet
            if (request[current] != LF) {
                processError("LF was expected at position $current")
                return
            }

            current++
            request.limit(request.position())
            request.position(current)
            request = ByteBuffer.allocate(length).apply { this.put(request.slice()) }
            state = READING_PAYLOAD
        }

        if (state == READING_PAYLOAD && !request.hasRemaining()) {
            // payload is completely read
            process()
        }
    }

    private fun process() {
        if (command == Command.PROCESS.code) {
            val output = controller.handle(request.array())
            response = ByteBuffer.wrap(output)
        }

        // write event registration
        key.interestOps(SelectionKey.OP_WRITE) // register writing event listening
        state = WRITING_RESPONSE

        resetRequest()
    }

    private fun processError(msg: String) {
        // TODO: What to do with unread data from input? We need to drain input before sending response
        val output = "-ERR $msg\r\n".toByteArray(Charsets.US_ASCII)
        response = ByteBuffer.wrap(output)

        // write event registration
        key.interestOps(SelectionKey.OP_WRITE) // register writing event listening
        state = WRITING_RESPONSE

        resetRequest()
    }

    private fun write() {
        channel.write(response)

        if (state == WRITING_RESPONSE && !response.hasRemaining()) {
            // output payload is written completely
            resetResponse()

            // register reading event listening
            key.interestOps(SelectionKey.OP_READ)
            state = READING_COMMAND
        }
    }

    private fun resetRequest() {
        command = 0
        length = 0
        current = 1
        request = ByteBuffer.allocate(HEADER_MAX_SIZE)
    }

    private fun resetResponse() {
        response = ByteBuffer.allocate(0)
    }

    private enum class State {
        READING_COMMAND,
        READING_LENGTH,
        READING_CR,
        READING_LF,
        READING_PAYLOAD,
        WRITING_RESPONSE
    }

    companion object {

        private const val HEADER_MAX_SIZE = 128
        private const val CR = 0x0d.toByte()
        private const val LF = 0x0a.toByte()

        fun create(channel: SocketChannel, selector: Selector): Handler {
            channel.configureBlocking(false)
            val key = channel.register(selector, SelectionKey.OP_READ)
            val messageConsumer = DefaultController()
            return Handler(key, channel, messageConsumer)
        }

    }

}
