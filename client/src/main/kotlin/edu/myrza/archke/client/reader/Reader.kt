package edu.myrza.archke.client.reader

interface Reader {

    fun read(chunk: ByteArray, occupied: Int)

    fun done(): Boolean

}
