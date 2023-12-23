package edu.myrza.archke.server.io

import edu.myrza.archke.server.controller.Dispatcher
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

class Acceptor (
    private val serverChannel: ServerSocketChannel,
    private val selector: Selector,
    private val dispatcher: Dispatcher
) : Runnable {

    override fun run() {
        val channel = serverChannel.accept().apply { configureBlocking(false) }
        val key = channel.register(selector, SelectionKey.OP_READ)
        val handler = Handler(key, channel, dispatcher)

        key.attach(handler)
    }

}
