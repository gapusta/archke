package edu.myrza.archke.server.io

import edu.myrza.archke.server.io.factory.HandlerFactory
import java.nio.channels.ServerSocketChannel
import java.nio.channels.spi.AbstractSelectableChannel

class Acceptor (
    private val channel: ServerSocketChannel,
    private val factory: HandlerFactory,
    private val handlers: MutableMap<AbstractSelectableChannel, Runnable>
) : Runnable {

    override fun run() {
        channel.accept().also { handlers[it] = factory.get(it) }
    }

}
