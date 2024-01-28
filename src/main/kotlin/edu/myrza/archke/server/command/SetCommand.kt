package edu.myrza.archke.server.command

import edu.myrza.archke.server.db.KeyValueStorage

class SetCommand(private val storage: KeyValueStorage) : Command {

    override fun command(): String = "SET"

    override fun handle(args: List<ByteArray>): Array<ByteArray> {
        if (args.size != 3) throw IllegalArgumentException("[SET] some arguments are missing")

        storage.set(args[1], args[2])

        return arrayOf("+OK\r\n".toByteArray(Charsets.US_ASCII))
    }

}
