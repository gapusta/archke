package edu.myrza.archke.server.io

import edu.myrza.archke.server.Server
import edu.myrza.archke.server.util.silentClose

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
        server.selector.silentClose()
        server.channel.silentClose()

        for (channel in server.clientChannels) {
            channel.use { it.shutdownOutput() }
        }
    }

}
