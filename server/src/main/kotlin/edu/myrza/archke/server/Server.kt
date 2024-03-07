package edu.myrza.archke.server

import edu.myrza.archke.server.command.ExistCommand
import edu.myrza.archke.server.command.GetCommand
import edu.myrza.archke.server.command.SetCommand
import edu.myrza.archke.server.controller.ControllerSupplierImpl
import edu.myrza.archke.server.io.Acceptor
import edu.myrza.archke.server.io.Reactor
import edu.myrza.archke.server.db.KeyValueStorageImpl
import java.io.IOException
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
        val service = KeyValueStorageImpl()

        // CONTROLLER LAYER
        val commands = listOf(
            SetCommand(service),
            GetCommand(service),
            ExistCommand(service)
        )
        val controllerSupplier = ControllerSupplierImpl(commands)

        // IO LAYER
        val selector = Selector.open()

        val serverChannel: ServerSocketChannel?
        try {
            serverChannel = ServerSocketChannel.open().apply {
                socket().bind(InetSocketAddress(port))
                configureBlocking(false)
            }
        } catch (ex: IOException) {
            println("Error during server start up : ${ex.message}")
            return
        }
        val key = serverChannel.register(selector, SelectionKey.OP_ACCEPT)
        val acceptor = Acceptor(serverChannel, selector, controllerSupplier)
        key.attach(acceptor)

        reactor = Reactor(selector)
    }

    private fun run() {
        val thread = Thread(reactor)
        thread.start()
    }

}
