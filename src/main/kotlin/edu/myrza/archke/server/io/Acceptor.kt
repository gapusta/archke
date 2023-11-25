package edu.myrza.archke.server.io

import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.spi.AbstractSelectableChannel

class Acceptor private constructor(
    private val selector: Selector,
    private val channel: ServerSocketChannel,
    private val processors: MutableMap<AbstractSelectableChannel, Runnable>
) : Runnable {

    override fun run() {
        channel.accept().also { processors[it] = Handler.create(it, selector) }
    }

    companion object {
        fun create(port: Int,
                   selector: Selector,
                   processors: MutableMap<AbstractSelectableChannel, Runnable>
        ): Acceptor {
            val serverChannel = ServerSocketChannel.open().apply {
                this.socket().bind(InetSocketAddress(port))
                this.configureBlocking(false)
                this.register(selector, SelectionKey.OP_ACCEPT)
            }
            return Acceptor(selector, serverChannel, processors).apply { processors[serverChannel] = this }
        }
    }
}
