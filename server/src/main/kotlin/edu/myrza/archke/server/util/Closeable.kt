package edu.myrza.archke.server.util

import java.io.Closeable

fun Closeable.silentClose() = try { this.close() } catch (_: Exception) { }
