package edu.myrza.archke.server.controller

import edu.myrza.archke.server.command.Command

class ControllerImpl(commands: List<Command>) : Controller {

    private val commands = commands.groupBy { it.command() }

    override fun handle(request: Array<ByteArray>): Array<ByteArray> {
        val command = String(request[0], Charsets.US_ASCII)

        return commands[command]!!.first().handle(request)
    }

}
