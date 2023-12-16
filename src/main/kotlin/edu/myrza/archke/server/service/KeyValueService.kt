package edu.myrza.archke.server.service

interface KeyValueService {

    fun set(key: ByteArray, value: ByteArray)

    fun get(key: ByteArray): ByteArray?

}
