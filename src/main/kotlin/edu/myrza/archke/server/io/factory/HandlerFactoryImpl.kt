package edu.myrza.archke.server.io.factory

import edu.myrza.archke.server.controller.Dispatcher
import edu.myrza.archke.server.io.Handler
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class HandlerFactoryImpl(
    private val selector: Selector,
    private val dispatcher: Dispatcher
) : HandlerFactory {

    override fun get(channel: SocketChannel): Handler {
        channel.configureBlocking(false)
        val key = channel.register(selector, SelectionKey.OP_READ)

        return Handler(key, channel, dispatcher)
    }

}
