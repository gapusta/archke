package edu.myrza.archke.server.controller

import edu.myrza.archke.server.controller.parser.Reader

class DispatcherImpl(controllers: List<Controller>) : Dispatcher {

    private val controllers = controllers.groupBy { it.command() }
    private var reader = Reader()

    override fun handle(chunk: ByteArray, length: Int): Array<ByteArray>? {
        reader.read(chunk, length)

        if (!reader.done()) return null
        if (reader.payload().isEmpty()) return null

        val payload = reader.payload()
        val command = String(payload[0], Charsets.US_ASCII)

        reader = Reader()

        return controllers[command]!!.first().handle(payload)
    }

}
