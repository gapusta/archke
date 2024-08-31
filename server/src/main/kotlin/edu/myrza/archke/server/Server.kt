package edu.myrza.archke.server

import edu.myrza.archke.server.controller.Controller
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

data class Server(
    var stop: Boolean,
    val selector: Selector,
    var channel: ServerSocketChannel,
    val controller: Controller,
    val clientChannels: MutableList<SocketChannel>
) {
    constructor(
        selector: Selector,
        channel: ServerSocketChannel,
        controller: Controller
    ) : this(
        stop = false,
        selector = selector,
        channel = channel,
        controller = controller,
        clientChannels = mutableListOf()
    )
}
