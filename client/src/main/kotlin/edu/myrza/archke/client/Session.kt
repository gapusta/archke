package edu.myrza.archke.client

import java.io.Closeable
import java.net.Socket

interface Session : Closeable {

    fun set(key: ByteArray, value: ByteArray): String

    fun get(key: ByteArray): ByteArray?

    override fun close()

    companion object {

        fun open(host: String, port: Int): Session {
            val socket = Socket(host, port)
//            socket.setSoLinger(true, 0) // Will trigger RST when closing the socket

            return SessionImpl(socket)
        }

    }

}
