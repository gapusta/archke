package edu.myrza.archke.server.io

import edu.myrza.archke.server.dispatcher.DispatcherFactory
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

class Acceptor (
    private val serverChannel: ServerSocketChannel,
    private val selector: Selector,
    private val factory: DispatcherFactory
) : Runnable {

    override fun run() {
        val channel = serverChannel.accept().apply { configureBlocking(false) }
        val key = channel.register(selector, SelectionKey.OP_READ)
        val dispatcher = factory.get()
        val handler = Handler(key, channel, dispatcher)

        key.attach(handler)
    }

}
