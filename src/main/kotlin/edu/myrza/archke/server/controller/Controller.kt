package edu.myrza.archke.server.controller

interface Controller {

    fun handle(payload: ByteArray): ByteArray

}
