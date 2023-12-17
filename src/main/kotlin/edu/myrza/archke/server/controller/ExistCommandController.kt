package edu.myrza.archke.server.controller

import edu.myrza.archke.server.service.GlobalKeyValueService

class ExistCommandController(private val service: GlobalKeyValueService) : Controller{

    override fun command(): String = "EXISTS"

    override fun handle(args: List<ByteArray>): Array<ByteArray> {
        if (args.size != 2) throw IllegalArgumentException("[EXISTS] some arguments are missing")

        val result = service.get(args[1])?.let { EXISTS } ?: NOT_EXISTS

        return arrayOf(result)
    }

    companion object {
        private val EXISTS = ":1\r\n".toByteArray(Charsets.US_ASCII)
        private val NOT_EXISTS = ":0\r\n".toByteArray(Charsets.US_ASCII)
    }
}
