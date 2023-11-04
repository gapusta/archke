package edu.myrza.archke

import edu.myrza.archke.client.Session
import edu.myrza.archke.server.Reactor

fun main(args: Array<String>) {
    // server example
    val reactor = Reactor.create(9999)
    val thread = Thread(reactor)
    thread.start()

    Thread.sleep(1000)

    // client example
    val session = Session.open("localhost", 9999)

    session.send("Test message number #1")
    session.send("Test message that I got #2")
    session.send("Test message is here for you #3")

    session.close()
}
