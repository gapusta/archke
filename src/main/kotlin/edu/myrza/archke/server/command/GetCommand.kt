package edu.myrza.archke.server.command

import edu.myrza.archke.server.db.KeyValueStorage

class GetCommand(private val storage: KeyValueStorage) : Command {

    override fun command(): String = "GET"

    override fun handle(args: List<ByteArray>): Array<ByteArray> {
        if (args.size != 2) throw IllegalArgumentException("[GET] some arguments are missing")

        val key = args[1]
        val value = storage.get(key) ?: throw IllegalArgumentException("No value found for key $key")
        val header = "$${value.size}\r\n".toByteArray(Charsets.US_ASCII)

        return arrayOf(header, value)
    }

}
