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

    private var input = ByteBuffer.wrap(ByteArray(BUFFER_SIZE))
    private var output = ArrayList<ByteBuffer>()

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
        val read = channel.read(input)
        var start = 0

        if (read == -1) { // client signaled he will not send anything (FIN, ACK)
            cleanUp()
            return
        }

        while (true) {
            start = reader.read(input.array(), start, input.position())

            if (!reader.done()) break // buffer does not contain the current command's full data

            process()
        }

        input.clear()
    }

    private fun process() {
        val result = controller.handle(reader.payload())

        reader = Reader()

        result.map { ByteBuffer.wrap(it) }.apply { output.addAll(this) }

        key.interestOps(SelectionKey.OP_WRITE)
        state = WRITE
    }

    private fun write() {
        // Writes the content of the buffers in the order/sequence they are encountered in the array (from 0 up to output.length).
        // Only data between "position" and "limit" of any particular buffer is written. If n bytes
        // are written from a buffer, the buffer's position p changes to p+n-1. Empty buffers (buffers with position equal to limit)
        // are ignored during the operation, regardless of their position in array.
        channel.write(output.toTypedArray())

        if (!output.last().hasRemaining()) {
            // output payload is written completely, clean up
            output.clear()

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
