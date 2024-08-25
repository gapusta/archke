package edu.myrza.archke.server.io

import edu.myrza.archke.server.controller.Controller
import edu.myrza.archke.server.controller.parser.Reader
import edu.myrza.archke.server.io.Handler.State.*
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel

class Handler (
    private val key: SelectionKey,
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
        } catch (ex: IOException) {
            // one of the possible causes of SocketException is RST (client abruptly closed the connection)
            println("Connection error during [ $state ] event processing : ${ex.message}")
            closeConnection()
        }
    }

    private fun read() {
        val read = channel.read(input)
        var start = 0

        if (read == -1) { // client signaled he will not send anything, client sent (FIN, ACK)
            closeConnection()
            return
        }

        while (true) {
            start = reader.read(input.array(), start, input.position())

            if (!reader.done()) break // buffer does not contain the current command's full data

            process(reader.payload())

            reader = Reader()
        }

        if (output.isNotEmpty()) {
            // There are complete responses to commands ready to be sent back to the client
            key.interestOps(SelectionKey.OP_WRITE)
            state = WRITE
        }

        input.clear()
    }

    private fun process(request: Array<ByteArray>) {
        val result = controller.handle(request).map { ByteBuffer.wrap(it) }

        output.addAll(result)
    }

    private fun write() {
        // Writes the content of the buffers in the order/sequence they are encountered in the array (from 0 up to output.length).
        // Only data between "position" and "limit" of any particular buffer is written. If n bytes
        // are written from a buffer, the buffer's position p changes to p+n-1. Empty buffers (buffers with position equal to limit)
        // are ignored during the operation, regardless of their position in array.
        channel.write(output.toTypedArray())

        if (!output.last().hasRemaining()) {
            // all the responses have been successfully sent, clean up
            output.clear()

            // register reading event listening
            key.interestOps(SelectionKey.OP_READ)
            state = READ
        }
    }

    private fun closeConnection() {
        try {
            channel.shutdownOutput() // Here, we send [FIN, ACK] back
            channel.close() // Here we release resources and implicitly cancel selection key
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
