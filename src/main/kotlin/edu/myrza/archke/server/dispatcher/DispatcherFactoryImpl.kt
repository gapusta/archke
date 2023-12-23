package edu.myrza.archke.server.dispatcher

import edu.myrza.archke.server.controller.Controller

class DispatcherFactoryImpl(private val controllers: List<Controller>) : DispatcherFactory {

    override fun get(): Dispatcher = DispatcherImpl(controllers)

}
