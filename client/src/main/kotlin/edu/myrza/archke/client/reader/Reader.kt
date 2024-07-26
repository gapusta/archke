package edu.myrza.archke.client.reader

interface Reader<T> {

    fun read(chunk: ByteArray, occupied: Int)

    fun done(): Boolean

    fun payload(): T

}
