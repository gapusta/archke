package edu.myrza.archke.server.io

import java.nio.channels.Selector
import java.nio.channels.spi.AbstractSelectableChannel

class Reactor private constructor (
    private val selector: Selector,
    private val processors: MutableMap<AbstractSelectableChannel, Runnable>,
) : Runnable {

    override fun run() {
        // Event loop
        while (!Thread.interrupted()) {
            selector.select()

            val selected = selector.selectedKeys()

            for(key in selected) {
                val channel = key.channel()

                processors[channel]!!.run()

                // keys can get cancelled during processor execution,
                // so we should remove processors of these keys
                if (!key.isValid) processors.remove(channel)
            }

            selected.clear()
        }
    }

    companion object {
        fun create(port: Int): Reactor {
            val selector = Selector.open()
            val processors = mutableMapOf<AbstractSelectableChannel, Runnable>()

            Acceptor.create(port, selector, processors)

            return Reactor(selector, processors)
        }
    }
}
