package edu.myrza.archke.util

import java.nio.ByteBuffer

/**
 *  Converts subpart of ByteBuffer into 32-bit Integer.
 *  Assumes big-endian byte order (most significant bytes come first)
 *  min value is 0
 *  max value is 2097152
*/
fun ByteBuffer.getPositiveInt(offset: Int): Int {
    return (this[offset].toInt() shl 24) or
            (this[offset + 1].toInt() shl 16) or
            (this[offset + 2].toInt() shl 8) or
            (this[offset + 3].toInt())
}

/**
 *  Returns byte representation of 32-bit Integer in big-endian byte order (most significant bytes come first)
 *  min value is 0
 *  max value is 2097152
 */
fun Int.getBytes(): ByteArray {
    val array = ByteArray(4)
    array[0] = (this shr 24).toByte()
    array[1] = (this shr 16).toByte()
    array[2] = (this shr 8).toByte()
    array[3] = this.toByte()
    return array
}
