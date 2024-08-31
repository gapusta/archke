package edu.myrza.archke.server

import edu.myrza.archke.server.command.*
import edu.myrza.archke.server.controller.ControllerImpl
import edu.myrza.archke.server.io.Acceptor
import edu.myrza.archke.server.io.Reactor
import edu.myrza.archke.server.db.KeyValueStorageImpl
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

class Archke(private val port: Int) {

    private lateinit var reactor: Reactor

    fun start() {
        assemble()
        run()
    }

    private fun assemble() {
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
        val server = Server(selector,serverChannel,controller)
        val acceptor = Acceptor(server)
        key.attach(acceptor)

        reactor = Reactor(server)

        // ADDITIONAL COMMANDS
        ShutdownCommand(server).apply { commands[command()] = this }
    }

    private fun run() {
        val thread = Thread(reactor)
        thread.start()
    }

}
