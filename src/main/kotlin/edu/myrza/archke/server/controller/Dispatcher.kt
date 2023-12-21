package edu.myrza.archke.server.controller

interface Dispatcher {

    /*
    * It reads request input chunk, if there was enough chunks read
    * it processes the request and returns an output as byte array as well.
    * Returns null if processing is not done yet
    * */
    fun handle(chunk: ByteArray, length: Int): Array<ByteArray>?

}
