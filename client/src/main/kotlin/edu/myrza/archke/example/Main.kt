package edu.myrza.archke.example

import edu.myrza.archke.client.Client
import java.util.regex.Pattern

fun main(args: Array<String>) {
    println("Session opening...")

    val host = "localhost"
    val port = 9999
    val client = Client.connect(host, port)

    println("Session opened")

    while (true) {
        print("> ")

        val line = readLine()!!.trim()

        if (line == "exit") break

        if (line.startsWith("SET", true)) {
            val words = line.split(Pattern.compile("[ ]+"))
            val key = words[1].toByteArray(Charsets.US_ASCII)
            val value = words[2].toByteArray(Charsets.UTF_8)
            val response = client.set(key, value)

            println(response)
            continue
        }

        if (line.startsWith("GET", true)) {
            val words = line.split(Pattern.compile("[ ]+"))
            val key = words[1].toByteArray(Charsets.US_ASCII)
            val value = client.get(key)?.let { String(it, Charsets.UTF_8) } ?: "(null)"

            println(value)
            continue
        }

        if (line.startsWith("EXISTS", true)) {
            val words = line.split(Pattern.compile("[ ]+"))
            val key = words[1].toByteArray(Charsets.US_ASCII)
            val value = client.exists(key)

            println(value)
            continue
        }

        if (line.startsWith("DEL", true)) {
            val words = line.split(Pattern.compile("[ ]+"))
            val key = words[1].toByteArray(Charsets.US_ASCII)
            val value = client.delete(key)

            println(value)
            continue
        }

        println("Unknown command")
    }

    client.close()

    println("Session closed")
}
