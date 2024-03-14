package edu.myrza.archke.server.controller

interface Controller {

    fun handle(request: Array<ByteArray>): Array<ByteArray>

}
