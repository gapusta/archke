package edu.myrza.archke.example

import edu.myrza.archke.client.Client
import edu.myrza.archke.client.exception.ClientException
import java.util.regex.Pattern

fun main(args: Array<String>) {
    println("Session opening...")

    val host = "localhost"
    val port = 9999
    val client = Client.connect(host, port)

    println("Session opened")

    try {
        client.use {
            while (true) {
                print("> ")

                val line = readln().trim()

                if (line == "exit") break

                if (line.startsWith("SET", true)) {
                    val words = line.split(Pattern.compile("[ ]+"))
                    val key = words[1].toByteArray(Charsets.US_ASCII)
                    val value = words[2].toByteArray(Charsets.UTF_8)

                    val response = if (words.size > 4) {
                        val timeout = words[4].toInt()
                        client.set(key, value, timeout)
                    } else {
                        client.set(key, value)
                    }

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

                if (line.startsWith("SHUTDOWN", true)) {
                    client.shutdown()
                    println("shutdown complete")
                    break
                }

                println("Unknown command")
            }
        }
    } catch (ex: Exception) {
        when(ex) {
            is ClientException.ConnectionClosed -> {
                println("Connection closed unexpectedly from server side")
            }
            else -> {
                println("Unexpected error ${ex.message}")
                ex.printStackTrace()
            }
        }
    }

    println("Session closed")
}
