package edu.myrza.archke.server.command

import edu.myrza.archke.server.db.KeyValueStorage

class DelCommand(private val storage: KeyValueStorage) : Command {

    override fun command(): String = "DEL"

    override fun handle(args: List<ByteArray>): Array<ByteArray> {
        if (args.size != 2) throw IllegalArgumentException("[DEL] some arguments are missing")

        storage.delete(args[1])

        return arrayOf("+OK\r\n".toByteArray(Charsets.US_ASCII))
    }

}
