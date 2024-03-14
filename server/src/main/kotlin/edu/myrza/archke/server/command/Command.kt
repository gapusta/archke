package edu.myrza.archke.server.command

interface Command {

    fun command(): String

    fun handle(args: Array<ByteArray>): Array<ByteArray>

}
