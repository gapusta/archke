package edu.myrza.archke.server.io

import edu.myrza.archke.server.controller.Controller
import edu.myrza.archke.server.controller.parser.Reader
import edu.myrza.archke.server.io.Handler.State.*
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel

class Handler (
    private var key: SelectionKey,
    private val channel: SocketChannel,
    private val controller: Controller
) : Runnable {

    private var state = READ

    private var inBuffer = ByteBuffer.wrap(ByteArray(BUFFER_SIZE))
    private var outBuffers = emptyArray<ByteBuffer>()

    private var reader = Reader()

    override fun run() {
        try {
            when (state) {
                READ -> read()
                WRITE -> write()
            }
        }
        catch (ex: IOException) {
            // one of the possible causes of SocketException is RST (client abruptly closed the connection)
            println("Connection error during [ $state ] event processing : ${ex.message}")
            cleanUp()
        }
    }

    private fun read() {
        val read = channel.read(inBuffer)

        if (read == -1) { // client signaled he will not send anything (FIN, ACK)
            cleanUp()
            return
        }

        reader.read(inBuffer.array(), inBuffer.position()).also { inBuffer.clear() }

        if (reader.done()) process()
    }

    private fun process() {
        val result = controller.handle(reader.payload())

        reader = Reader()

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

    private fun cleanUp() {
        try {
            channel.close() // will also implicitly cancel selection key
            println("Connection closed")
        } catch (ex: IOException) {
            println("Error during connection closing : ${ex.message}")
        }
    }

    private enum class State { READ, WRITE }

    companion object {
        private const val BUFFER_SIZE = 128 * 1024 // 128 KB
    }
}
