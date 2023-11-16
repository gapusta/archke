package edu.myrza.archke.example.client.console

import edu.myrza.archke.client.Session

fun main(args: Array<String>) {
    // 1. open session
    println("Session opening...")

    val host = "localhost"
    val port = 9999
    val session = Session.open(host, port)

    println("Session has been opened successfully [ host : $host, port : $port ]")

    // 2. read input string
    while (true) {
        print("> ")

        val message = readLine() ?: break

        if (message == "exit") break

        // 3. send to the server
        session.send(message)
    }

    println("Session close...")

    session.close()

    println("Session has been closed successfully")
}
