package edu.myrza.archke.server

import edu.myrza.archke.server.consumer.MessageConsumer
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class Reactor private constructor (
    private val selector: Selector,
    private val serverSocketChannel: ServerSocketChannel,
    private val consumer: MessageConsumer
) : Runnable {

    private val processors = mutableMapOf<SocketChannel, Runnable>()

    override fun run() {
        while (!Thread.interrupted()) {
            selector.select()

            val selected = selector.selectedKeys()

            for(key in selected) {
                if (key.isAcceptable) {
                    println("INFO : Accept event")
                    serverSocketChannel.accept().also { processors[it] = Processor.create(it, selector, consumer) }
                    println("INFO : New processor created")
                } else {
                    val channel = key.channel()
                    processors[channel]!!.run()
                    if (!key.isValid) {
                        // key could become cancelled during processor execution, so we should check for it
                        processors.remove(channel)
                    }
                }
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

            return Reactor(selector, serverChannel, consumer)
        }

    }

}
