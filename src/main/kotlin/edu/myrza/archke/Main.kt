package edu.myrza.archke

import edu.myrza.archke.server.Server

fun main(args: Array<String>) {
    val server = Server(9999)
    server.start()
}
