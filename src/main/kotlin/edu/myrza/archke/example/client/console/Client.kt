package edu.myrza.archke.example.client.console

import edu.myrza.archke.client.Session
import java.util.regex.Pattern

fun main(args: Array<String>) {
    println("Session opening...")

    val host = "localhost"
    val port = 9999
    val session = Session.open(host, port)

    println("Session opened")

    while (true) {
        print("> ")

        val line = readLine()!!.trim()

        if (line == "exit") break

        if (line.startsWith("SET")) {
            val words = line.split(Pattern.compile("[ ]+"))
            val key = words[1].toByteArray(Charsets.US_ASCII)
            val value = words[2].toByteArray(Charsets.UTF_8)
            val response = session.set(key, value)

            println("[SET] $response")
        }

        if (line.startsWith("GET")) {
            val words = line.split(Pattern.compile("[ ]+"))
            val key = words[1].toByteArray(Charsets.US_ASCII)
            val response = session.get(key)
            val value = String(response, Charsets.UTF_8)

            println("[GET] $value")
        }
    }

    session.close()

    println("Session closed")
}
