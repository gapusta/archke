package edu.myrza.archke.server.db

import edu.myrza.archke.server.db.util.ByteArrayKey

class KeyValueStorageImpl : KeyValueStorage {

    private val map = mutableMapOf<ByteArrayKey, ByteArray>()

    override fun set(key: ByteArray, value: ByteArray) {
        map[ByteArrayKey(key)] = value
    }

    override fun delete(key: ByteArray): ByteArray? = map.remove(ByteArrayKey(key))

    override fun get(key: ByteArray): ByteArray? = map[ByteArrayKey(key)]

}
