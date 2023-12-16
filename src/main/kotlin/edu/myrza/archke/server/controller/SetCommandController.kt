package edu.myrza.archke.server.controller

import edu.myrza.archke.server.service.KeyValueService

class SetCommandController(private val keyValueService: KeyValueService) : Controller {

    override fun command(): String = "SET"

    override fun handle(args: List<ByteArray>): Array<ByteArray> {
        if (args.size < 3) throw IllegalArgumentException("[SET] some arguments are missing")

        keyValueService.set(args[1], args[2])

        // returns simple string
        return arrayOf("+OK\r\n".toByteArray(Charsets.US_ASCII))
    }

}
