package edu.myrza.archke.server

import edu.myrza.archke.server.controller.DispatcherImpl
import edu.myrza.archke.server.controller.GetCommandController
import edu.myrza.archke.server.controller.SetCommandController
import edu.myrza.archke.server.io.Acceptor
import edu.myrza.archke.server.io.Reactor
import edu.myrza.archke.server.io.factory.HandlerFactoryImpl
import edu.myrza.archke.server.service.GlobalKeyValueServiceImpl
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.spi.AbstractSelectableChannel

class Server(private val port: Int) {

    private lateinit var reactor: Reactor

    fun start() {
        assemble()
        run()
    }

    private fun assemble() {
        // SERVICE LAYER
        val service = GlobalKeyValueServiceImpl()

        // CONTROLLER LAYER
        val controllers = listOf(SetCommandController(service), GetCommandController(service))
        val dispatcher = DispatcherImpl(controllers)

        // IO LAYER
        val selector = Selector.open()
        val handlers = mutableMapOf<AbstractSelectableChannel, Runnable>()
        val serverChannel = ServerSocketChannel.open().apply {
            socket().bind(InetSocketAddress(port))
            configureBlocking(false)
            register(selector, SelectionKey.OP_ACCEPT)
        }
        val handlerFactory = HandlerFactoryImpl(selector, dispatcher) // handlers

        val acceptor = Acceptor(serverChannel, handlerFactory, handlers)
        handlers[serverChannel] = acceptor

        reactor = Reactor(selector, handlers)
    }

    private fun run() {
        val thread = Thread(reactor)
        thread.start()
    }

}
