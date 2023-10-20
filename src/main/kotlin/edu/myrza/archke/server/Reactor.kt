package edu.myrza.archke.server

import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class Reactor(
    private val selector: Selector,
    private val serverSocketChannel: ServerSocketChannel
): Runnable {

    private val processors = mutableMapOf<SocketChannel, Runnable>()

    override fun run() {
        while (!Thread.interrupted()) {
            selector.select()

            val selected = selector.selectedKeys()

            for(key in selected) {
                if (key.isAcceptable) {
                    serverSocketChannel.accept().also {
                        processors[it] = Processor.create(it, selector)
                    }
                } else {
                    processors[key.channel()]!!.run()
                }
            }

            selected.clear()
        }
    }

    companion object {

        fun create(port: Int): Reactor {
            val selector = Selector.open()
            val serverSocketChannel = ServerSocketChannel.open().apply {
                this.socket().bind(InetSocketAddress(port))
                this.configureBlocking(false)
                this.register(selector, SelectionKey.OP_ACCEPT)
            }

            return Reactor(selector, serverSocketChannel)
        }

    }

}
