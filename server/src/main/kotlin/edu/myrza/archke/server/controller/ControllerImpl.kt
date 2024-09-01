package edu.myrza.archke.server.controller

import edu.myrza.archke.server.command.Command

class ControllerImpl(private val commands: Map<String, Command>) : Controller {

    override fun handle(request: Array<ByteArray>): Array<ByteArray> {
        val command = String(request[0], Charsets.US_ASCII)

        return commands[command]?.handle(request)
            ?: arrayOf("-UNKNOWN_COMMAND $command".toByteArray(Charsets.US_ASCII))
    }

}
