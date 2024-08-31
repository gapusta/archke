package edu.myrza.archke.server.command

import edu.myrza.archke.server.Server

class ShutdownCommand(private val server: Server) : Command {

    override fun command() : String = "SHUTDOWN"

    override fun handle(args: Array<ByteArray>): Array<ByteArray> {
        server.stop = true

        return emptyArray()
    }
}
