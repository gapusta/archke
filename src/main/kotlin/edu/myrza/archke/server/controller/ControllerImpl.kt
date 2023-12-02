package edu.myrza.archke.server.controller

import edu.myrza.archke.server.controller.parser.Reader
import edu.myrza.archke.server.controller.dto.Result
import edu.myrza.archke.server.controller.dto.Result.STATUS.*

class ControllerImpl : Controller {

    private var reader = Reader()

    override fun handle(chunk: ByteArray, length: Int): Result {
        reader.read(chunk, length)

        if(!reader.done()) return Result(NOT_DONE, ByteArray(0))

        val message = String(reader.payload(), Charsets.UTF_8)

        println("MESSAGE : $message")

        reader = Reader()

        val output = "+OK\r\n".toByteArray(Charsets.US_ASCII)

        return Result(DONE, output)
    }

}
