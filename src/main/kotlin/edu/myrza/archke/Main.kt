package edu.myrza.archke

import edu.myrza.archke.client.Session
import edu.myrza.archke.server.Reactor
import edu.myrza.archke.server.consumer.Printer

fun main(args: Array<String>) {
    // server example
    val reactor = Reactor.create(9999, Printer())
    val thread = Thread(reactor)
    thread.start()

    // client example
    val session = Session.open("localhost", 9999)
    session.send("Hi")
    session.send("message example #1")
    session.send("message example #2")
    session.close()
}
