package edu.myrza.archke.server.controller

interface Controller {

    fun command(): String

    fun handle(args: List<ByteArray>): Array<ByteArray>

}
