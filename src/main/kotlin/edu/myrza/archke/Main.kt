package edu.myrza.archke

import edu.myrza.archke.server.Reactor
import edu.myrza.archke.server.consumer.Printer

fun main(args: Array<String>) {
    val reactor = Reactor.create(9999, Printer())
    val thread = Thread(reactor)

    thread.start()
}
