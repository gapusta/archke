package edu.myrza.archke.server.command

interface Command {

    fun command(): String

    fun handle(args: List<ByteArray>): Array<ByteArray>

}
