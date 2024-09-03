package edu.myrza.archke.server.command

import edu.myrza.archke.server.db.KeyValueStorage
import edu.myrza.archke.server.command.Constants.NULL

class GetCommand(private val storage: KeyValueStorage) : Command {

    override fun command(): String = "GET"

    override fun handle(args: Array<ByteArray>): Array<ByteArray> {
        if (args.size != 2) return arrayOf("-INVALID_ARGUMENTS\r\n".toByteArray(Charsets.US_ASCII))

        val key = args[1]
        val value = storage.get(key) ?: return arrayOf(NULL)
        val header = "$${value.size}\r\n".toByteArray(Charsets.US_ASCII)

        return arrayOf(header, value)
    }

}
