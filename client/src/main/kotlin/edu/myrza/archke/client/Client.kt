package edu.myrza.archke.client

import java.io.Closeable
import java.net.Socket

interface Client : Closeable {

    fun set(key: ByteArray, value: ByteArray): String

    fun set(key: ByteArray, value: ByteArray, timeout: Int): String

    fun get(key: ByteArray): ByteArray?

    fun delete(key: ByteArray): Int

    fun exists(key: ByteArray): Boolean

    fun shutdown()

    override fun close()

    companion object {

        fun connect(host: String, port: Int): Client {
            val socket = Socket(host, port)
            // socket.setSoLinger(true, 0) // Will trigger RST when closing the socket

            return ClientImpl(socket)
        }

    }

}
