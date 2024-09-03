package edu.myrza.archke.server.command

import edu.myrza.archke.server.db.KeyValueStorage

class DelCommand(private val storage: KeyValueStorage) : Command {

    override fun command(): String = "DEL"

    override fun handle(args: Array<ByteArray>): Array<ByteArray> {
        if (args.size != 2) return arrayOf("-INVALID_ARGUMENTS\r\n".toByteArray(Charsets.US_ASCII))

        val key = args[1]
        val result = storage.delete(key)?.let { DELETED } ?: NOT_FOUND

        return arrayOf(result)
    }

    companion object {
        private val DELETED = ":1\r\n".toByteArray(Charsets.US_ASCII) // one value has been deleted
        private val NOT_FOUND = ":0\r\n".toByteArray(Charsets.US_ASCII) // zero values have been deleted
    }

}
