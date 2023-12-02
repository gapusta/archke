package edu.myrza.archke.server.io

import edu.myrza.archke.server.io.Handler.State.*
import edu.myrza.archke.server.controller.Controller
import edu.myrza.archke.server.controller.ControllerImpl
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class Handler private constructor (
    private var key: SelectionKey,
    private val channel: SocketChannel,
    private val controller: Controller
) : Runnable {

    private var state = READ
    private var backingArray = ByteArray(BUFFER_SIZE)
    private var buffer = ByteBuffer.wrap(backingArray)

    override fun run() {
        when (state) {
            READ -> read()
            WRITE -> write()
        }
    }

    private fun read() {
        val read = channel.read(buffer)

        if (read == -1) { // client signaled he will not send anything (FIN, ACK)
            channel.close()
            println("INFO : Connection closed")
            return
        }

        process()
    }

    private fun process() {
        // if the request handling is done, there must always be some kind of response (output.size != 0)
        val result = controller.handle(buffer.array(), buffer.position())

        if (!result.done()) {
            buffer.clear() // get rid of already processed chunk
            return
        }

        buffer = ByteBuffer.wrap(result.output)
        key.interestOps(SelectionKey.OP_WRITE)
        state = WRITE
    }

    private fun write() {
        channel.write(buffer)

        if (!buffer.hasRemaining()) {
            // output payload is written completely
            buffer = ByteBuffer.wrap(backingArray)

            // register reading event listening
            key.interestOps(SelectionKey.OP_READ)
            state = READ
        }
    }

    private enum class State { READ, WRITE }

    companion object {

        private const val BUFFER_SIZE = 128 * 1024; // 128 KB

        fun create(channel: SocketChannel, selector: Selector): Handler {
            channel.configureBlocking(false)
            val key = channel.register(selector, SelectionKey.OP_READ)
            val messageConsumer = ControllerImpl()
            return Handler(key, channel, messageConsumer)
        }

    }

}
