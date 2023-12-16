package edu.myrza.archke.server.service

class KeyValueServiceImpl : KeyValueService {

    private val map = mutableMapOf<ByteArray, ByteArray>()

    override fun set(key: ByteArray, value: ByteArray) { map[key] = value }

    override fun get(key: ByteArray): ByteArray? = map[key]

}