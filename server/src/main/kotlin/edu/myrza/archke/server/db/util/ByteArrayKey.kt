package edu.myrza.archke.server.db.util

class ByteArrayKey(private val array: ByteArray) {

    fun array(): ByteArray = array

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayKey

        return array.contentEquals(other.array)
    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }

}
