package edu.myrza.archke.example.client.console

import edu.myrza.archke.client.Session

fun main(args: Array<String>) {
    println("Session opening...")

    val host = "localhost"
    val port = 9999
    val session = Session.open(host, port)

    println("Session opened")

    val key = "MYKEY".toByteArray(Charsets.US_ASCII)
    val value = "MYVALUE".toByteArray(Charsets.US_ASCII)

    session.set(key, value)
    val response = session.get(key)

    println(String(response, Charsets.US_ASCII))

    session.close()

    println("Session closed")
}
