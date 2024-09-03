package edu.myrza.archke.server.command

import edu.myrza.archke.server.db.KeyValueStorage

class ExistCommand(private val storage: KeyValueStorage) : Command{

    override fun command(): String = "EXISTS"

    override fun handle(args: Array<ByteArray>): Array<ByteArray> {
        if (args.size != 2) return arrayOf("-INVALID_ARGUMENTS\r\n".toByteArray(Charsets.US_ASCII))

        val key = args[1]
        val result = storage.get(key)?.let { EXISTS } ?: NOT_EXISTS

        return arrayOf(result)
    }

    companion object {
        private val EXISTS = "#t\r\n".toByteArray(Charsets.US_ASCII)
        private val NOT_EXISTS = "#f\r\n".toByteArray(Charsets.US_ASCII)
    }
}
