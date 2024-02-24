package edu.myrza.archke.server.io

import edu.myrza.archke.server.controller.ControllerSupplier
import java.io.IOException
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class Acceptor (
    private val serverChannel: ServerSocketChannel,
    private val selector: Selector,
    private val controllerSupplier: ControllerSupplier
) : Runnable {

    override fun run() {
        val channel: SocketChannel?
        try {
            channel = serverChannel.accept().apply { configureBlocking(false) }
        } catch (ex: IOException) {
            println("Connection error during [ ACCEPT ] event processing : ${ex.message}")
            return
        }
        val key = channel.register(selector, SelectionKey.OP_READ)
        val controller = controllerSupplier.get()
        val handler = Handler(key, channel, controller)

        key.attach(handler)
    }

}
