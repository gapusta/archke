package edu.myrza.archke.server

import edu.myrza.archke.server.command.*
import edu.myrza.archke.server.config.ArchkeConfig
import edu.myrza.archke.server.controller.ControllerImpl
import edu.myrza.archke.server.io.Acceptor
import edu.myrza.archke.server.io.Reactor
import edu.myrza.archke.server.db.KeyValueStorageImpl
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

class Archke(private val archkeConfig: ArchkeConfig) {

    fun start() {
        try {
            val reactor = assemble()
            run(reactor)
        } catch (ex: Exception) {
            println("Error during server start up : ${ex.message}")
            return
        }
    }

    private fun assemble(): Reactor {
        // SERVICE LAYER
        val service = KeyValueStorageImpl()

        // CONTROLLER LAYER
        val commands = mutableMapOf<String, Command>().apply {
            SetCommand(service).also { this[it.command()] = it }
            GetCommand(service).also { this[it.command()] = it }
            DelCommand(service).also { this[it.command()] = it }
            ExistCommand(service).also { this[it.command()] = it }
        }
        val controller = ControllerImpl(commands)

        // IO LAYER
        val selector = Selector.open()

        val serverChannel = ServerSocketChannel.open().apply {
            socket().bind(InetSocketAddress(archkeConfig.port))
            configureBlocking(false)
        }
        val server = Server(selector, serverChannel, controller)
        val acceptor = Acceptor(server)

        val key = serverChannel.register(selector, SelectionKey.OP_ACCEPT)
        key.attach(acceptor)

        // ADDITIONAL COMMANDS
        ShutdownCommand(server).apply { commands[command()] = this }

        return Reactor(server)
    }

    private fun run(reactor: Reactor) {
        val thread = Thread(reactor)
        thread.start()
    }

}
