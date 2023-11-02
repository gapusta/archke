package edu.myrza.archke.server

import edu.myrza.archke.server.consumer.MessageConsumer
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.spi.AbstractSelectableChannel

class Acceptor private constructor(
    private val selector: Selector,
    private val channel: ServerSocketChannel,
    private val processors: MutableMap<AbstractSelectableChannel, Runnable>,
    private val consumer: MessageConsumer,
) : Runnable {

    override fun run() {
        channel.accept().also { processors[it] = Processor.create(it, selector, consumer) }
    }

    companion object {
        fun create(selector: Selector,
                   channel: ServerSocketChannel,
                   processors: MutableMap<AbstractSelectableChannel, Runnable>,
                   consumer: MessageConsumer): Acceptor = Acceptor(selector, channel, processors, consumer)
    }
}
