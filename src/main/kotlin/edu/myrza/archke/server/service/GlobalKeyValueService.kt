package edu.myrza.archke.server.service

interface GlobalKeyValueService {

    fun set(key: ByteArray, value: ByteArray)

    fun delete(key: ByteArray)

    fun get(key: ByteArray): ByteArray?

}
