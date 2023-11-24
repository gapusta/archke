package edu.myrza.archke

import edu.myrza.archke.server.io.Reactor

fun main(args: Array<String>) {
    val reactor = Reactor.create(9999)
    val thread = Thread(reactor)
    thread.start()
}
