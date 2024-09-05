package edu.myrza.archke.server.io

import edu.myrza.archke.server.Server
import java.io.IOException
import java.nio.channels.SelectionKey
import java.nio.channels.SocketChannel

class Acceptor (private val server: Server) : Runnable {

    override fun run() {
        val channel: SocketChannel?
        try {
            channel = server.channel.accept().apply { configureBlocking(false) }
        } catch (ex: IOException) {
            println("Connection error during [ ACCEPT ] event processing : ${ex.message}")
            return
        }
        val key = channel.register(server.selector, SelectionKey.OP_READ)
        val handler = Handler(key, channel, server.controller)

        key.attach(handler)
    }

}
