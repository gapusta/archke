package edu.myrza.archke.client

import java.io.Closeable
import java.net.Socket

interface Session : Closeable {

    fun send(msg: String)

    override fun close()

    companion object {

        fun open(host: String, port: Int): Session {
            val socket = Socket(host, port)

            return SessionImpl(socket)
        }

    }

}
