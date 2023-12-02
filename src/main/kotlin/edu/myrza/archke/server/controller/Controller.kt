package edu.myrza.archke.server.controller

import edu.myrza.archke.server.controller.dto.Result

interface Controller {

    /*
    * It reads request input chunk, if there was enough chunks read
    * it processes the request and returns an output as byte array as well
    * */
    fun handle(chunk: ByteArray, length: Int): Result

}
