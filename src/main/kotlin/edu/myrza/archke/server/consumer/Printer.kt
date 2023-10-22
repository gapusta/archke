package edu.myrza.archke.server.consumer

class Printer : MessageConsumer {

    override fun consume(payload: ByteArray): ByteArray {
        // decode input
        val message = String(payload, Charsets.UTF_8)
        // process
        println(message)
        // encode output
        return ByteArray(0)
    }

}
