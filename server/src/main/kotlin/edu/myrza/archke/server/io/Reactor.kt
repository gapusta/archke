package edu.myrza.archke.server.io

import java.nio.channels.Selector

class Reactor (private val selector: Selector) : Runnable {

    override fun run() {
        // Event loop
        while (!Thread.interrupted()) {
            selector.select()

            val selected = selector.selectedKeys()

            for(key in selected) {
                val handler = key.attachment() as Runnable

                handler.run()
            }

            selected.clear()
        }
    }

}
