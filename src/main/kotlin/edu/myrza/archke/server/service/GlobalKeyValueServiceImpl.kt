package edu.myrza.archke.server.service

class GlobalKeyValueServiceImpl : GlobalKeyValueService {

    private val map = mutableMapOf<Int, ByteArray>()

    override fun set(key: ByteArray, value: ByteArray) {
        map[key.contentHashCode()] = value
    }

    override fun delete(key: ByteArray) {
        map.remove(key.contentHashCode())
    }

    override fun get(key: ByteArray): ByteArray? = map[key.contentHashCode()]

}
