package edu.myrza.archke.server.controller

import edu.myrza.archke.server.command.Command
import edu.myrza.archke.server.controller.parser.Reader

class ControllerImpl(commands: List<Command>) : Controller {

    private val commands = commands.groupBy { it.command() }
    private var reader = Reader()

    override fun handle(chunk: ByteArray, length: Int): Array<ByteArray>? {
        reader.read(chunk, length)

        if (!reader.done()) return null
        if (reader.payload().isEmpty()) return null

        val payload = reader.payload()
        val command = String(payload[0], Charsets.US_ASCII)

        reader = Reader()

        return commands[command]!!.first().handle(payload)
    }

}
