package edu.myrza.archke.example

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

            println(response)
        }

        if (line.startsWith("GET")) {
            val words = line.split(Pattern.compile("[ ]+"))
            val key = words[1].toByteArray(Charsets.US_ASCII)
            val value = session.get(key)?.let { String(it, Charsets.UTF_8) } ?: "(null)"

            println(value)
        }

        if (line.startsWith("EXISTS")) {
            val words = line.split(Pattern.compile("[ ]+"))
            val key = words[1].toByteArray(Charsets.US_ASCII)
            val value = session.exists(key)

            println(value)
        }
    }

    session.close()

    println("Session closed")
}
