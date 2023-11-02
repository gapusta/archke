package edu.myrza.archke.server

import edu.myrza.archke.server.consumer.MessageConsumer
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.spi.AbstractSelectableChannel

class Reactor private constructor (
    private val selector: Selector,
    private val processors: MutableMap<AbstractSelectableChannel, Runnable>,
) : Runnable {

    override fun run() {
        // Event loop
        while (!Thread.interrupted()) {
            selector.select()

            val selected = selector.selectedKeys()

            for(key in selected) {
                val channel = key.channel()

                processors[channel]!!.run()

                // keys can get cancelled during processor execution,
                // so we should remove processors of these keys
                if (!key.isValid) processors.remove(channel)
            }

            selected.clear()
        }
    }

    companion object {
        fun create(port: Int, consumer: MessageConsumer): Reactor {
            val selector = Selector.open()
            val serverChannel = ServerSocketChannel.open().apply {
                this.socket().bind(InetSocketAddress(port))
                this.configureBlocking(false)
                this.register(selector, SelectionKey.OP_ACCEPT)
            }
            val processors = mutableMapOf<AbstractSelectableChannel, Runnable>()

            processors[serverChannel] = Acceptor.create(selector, serverChannel, processors, consumer)

            return Reactor(selector, processors)
        }
    }
}
