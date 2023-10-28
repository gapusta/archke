package edu.myrza.archke.server.consumer

class Printer : MessageConsumer {

    override fun consume(payload: ByteArray): ByteArray {
        // 1. decode input
        val message = String(payload, Charsets.UTF_8)
        // 2. process
        println("MESSAGE : $message")
        // 3. encode output (in this case, we do not need to)
        // 4. return output
        return ByteArray(0)
    }

}
