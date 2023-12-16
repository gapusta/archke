package edu.myrza.archke.server.io

import edu.myrza.archke.server.io.Handler.State.*
import edu.myrza.archke.server.controller.DispatcherController
import edu.myrza.archke.server.controller.DispatcherControllerImpl
import edu.myrza.archke.server.controller.GetCommandController
import edu.myrza.archke.server.controller.SetCommandController
import edu.myrza.archke.server.service.KeyValueServiceImpl
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class Handler private constructor (
    private var key: SelectionKey,
    private val channel: SocketChannel,
    private val controller: DispatcherController
) : Runnable {

    private var state = READ
    private var backingArray = ByteArray(BUFFER_SIZE)

    private var inBuffer = ByteBuffer.wrap(backingArray)
    private var outBuffers = emptyArray<ByteBuffer>()

    override fun run() {
        when (state) {
            READ -> read()
            WRITE -> write()
        }
    }

    private fun read() {
        val read = channel.read(inBuffer)

        if (read == -1) { // client signaled he will not send anything (FIN, ACK)
            channel.close()
            println("INFO : Connection closed")
            return
        }

        process()
    }

    private fun process() {
        val result = controller.handle(inBuffer.array(), inBuffer.position())

        inBuffer.clear()

        if (result == null) return

        outBuffers = result.map { ByteBuffer.wrap(it) }.toTypedArray()

        key.interestOps(SelectionKey.OP_WRITE)
        state = WRITE
    }

    private fun write() {
        channel.write(outBuffers)

        if (!outBuffers.last().hasRemaining()) {
            // output payload is written completely
            outBuffers = emptyArray()

            // register reading event listening
            key.interestOps(SelectionKey.OP_READ)
            state = READ
        }
    }

    private enum class State { READ, WRITE }

    companion object {

        private const val BUFFER_SIZE = 128 * 1024 // 128 KB

        fun create(channel: SocketChannel, selector: Selector): Handler {
            channel.configureBlocking(false)
            val key = channel.register(selector, SelectionKey.OP_READ)
            val service = KeyValueServiceImpl()
            val messageConsumer = DispatcherControllerImpl(listOf(
                SetCommandController(service),
                GetCommandController(service)
            ))
            return Handler(key, channel, messageConsumer)
        }

    }

}
