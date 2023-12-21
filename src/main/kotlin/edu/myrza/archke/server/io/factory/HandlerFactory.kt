package edu.myrza.archke.server.io.factory

import java.nio.channels.SocketChannel

interface HandlerFactory {

    fun get(channel: SocketChannel): Runnable

}
