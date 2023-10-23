package edu.myrza.archke.client

import java.io.Closeable

interface Session: Closeable {

    fun send(msg: String)

    override fun close()

    companion object {

        // TODO : do we need a builder here (builder that takes configurations as parameters)?
        fun open(host: String, port: Int): Session { return SessionImpl() }

    }

}
