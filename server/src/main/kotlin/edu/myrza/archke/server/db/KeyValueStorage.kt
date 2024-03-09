package edu.myrza.archke.server.db

interface KeyValueStorage {

    fun set(key: ByteArray, value: ByteArray)

    fun delete(key: ByteArray): ByteArray?

    fun get(key: ByteArray): ByteArray?

}
