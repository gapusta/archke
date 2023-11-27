package edu.myrza.archke.server.controller

class DefaultController : Controller {

    override fun handle(payload: ByteArray): ByteArray {
        if (payload.isNotEmpty()) {
            // 1. decode input
            val message = String(payload, Charsets.UTF_8)
            // 2. process
            println("MESSAGE : $message")
        }
        // 3. encode output (in this case, we do not need to)
        // 4. return output
        return "+OK\r\n".toByteArray(Charsets.US_ASCII)
    }

}
