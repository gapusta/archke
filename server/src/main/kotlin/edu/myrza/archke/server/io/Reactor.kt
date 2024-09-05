package edu.myrza.archke.server.io

import edu.myrza.archke.server.Server
import java.io.Closeable
import java.nio.channels.SocketChannel

class Reactor (private val server: Server) : Runnable {

    override fun run() {
        // Main event loop
        main@ while (true) {
            server.selector.select()

            val selected = server.selector.selectedKeys()

            for(key in selected) {
                val handler = key.attachment() as Runnable

                handler.run()

                if (server.stop) {
                    stop()
                    break@main
                }
            }

            selected.clear()
        }
    }

    private fun stop() {
        server.channel.silentClose()
        server.selector.use {
            val channels = server.selector.keys().mapNotNull { it.channel() as? SocketChannel }
            for (channel in channels) {
                channel.use { it.shutdownInput() }
            }
        }
    }

    private fun Closeable.silentClose() = try { this.close() } catch (_: Exception) { }

}
