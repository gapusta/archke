package edu.myrza.archke.util

import java.nio.ByteBuffer

/**
 *  Converts subpart of ByteBuffer into 32-bit Integer.
 *  Assumes big-endian byte order (most significant bytes come first)
 *  min value is 0
 *  max value is 2097152
*/
fun ByteBuffer.toPositiveInt(offset: Int): Int {
    return (this[offset].toInt() shl 24) or
            (this[offset + 1].toInt() shl 16) or
            (this[offset + 2].toInt() shl 8) or
            (this[offset + 3].toInt())
}
