package edu.myrza.archke.server

import edu.myrza.archke.server.controller.DispatcherImpl
import edu.myrza.archke.server.controller.GetCommandController
import edu.myrza.archke.server.controller.SetCommandController
import edu.myrza.archke.server.io.Acceptor
import edu.myrza.archke.server.io.Reactor
import edu.myrza.archke.server.service.GlobalKeyValueServiceImpl
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

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

        val serverChannel = ServerSocketChannel.open().apply {
            socket().bind(InetSocketAddress(port))
            configureBlocking(false)
        }
        val key = serverChannel.register(selector, SelectionKey.OP_ACCEPT)
        val acceptor = Acceptor(serverChannel, selector, dispatcher)
        key.attach(acceptor)

        reactor = Reactor(selector)
    }

    private fun run() {
        val thread = Thread(reactor)
        thread.start()
    }

}
