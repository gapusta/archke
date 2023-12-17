package edu.myrza.archke.server.controller

import edu.myrza.archke.server.service.GlobalKeyValueService

class DelCommandController(private val service: GlobalKeyValueService) : Controller {

    override fun command(): String = "DEL"

    override fun handle(args: List<ByteArray>): Array<ByteArray> {
        if (args.size != 2) throw IllegalArgumentException("[DEL] some arguments are missing")

        service.delete(args[1])

        return arrayOf("+OK\r\n".toByteArray(Charsets.US_ASCII))
    }

}
