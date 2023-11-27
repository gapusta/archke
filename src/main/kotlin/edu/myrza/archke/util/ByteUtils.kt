package edu.myrza.archke.util

// from - inclusive
// to - exclusive
fun ByteArray.findEnd(from: Int, to: Int): Int? {
    val cr = 0x0d.toByte()
    val lf = 0x0a.toByte()

    return (from until to)
        .map { index -> Triple(this[index], this[minOf(index + 1, lastIndex)], index) }
        .find { it.first == cr && it.second == lf }
        ?.third
}
