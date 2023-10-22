package edu.myrza.archke.server.consumer

interface MessageConsumer {

    fun consume(payload: ByteArray): ByteArray

}
