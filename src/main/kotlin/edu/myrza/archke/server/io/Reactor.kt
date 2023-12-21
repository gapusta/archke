package edu.myrza.archke.server.io

import java.nio.channels.Selector
import java.nio.channels.spi.AbstractSelectableChannel

class Reactor (
    private val selector: Selector,
    private val handlers: MutableMap<AbstractSelectableChannel, Runnable>,
) : Runnable {

    override fun run() {
        // Event loop
        while (!Thread.interrupted()) {
            selector.select()

            val selected = selector.selectedKeys()

            for(key in selected) {
                val channel = key.channel()

                handlers[channel]!!.run()

                // keys can get cancelled during processor execution,
                // so we should remove processors of these keys
                if (!key.isValid) handlers.remove(channel)
            }

            selected.clear()
        }
    }

}
