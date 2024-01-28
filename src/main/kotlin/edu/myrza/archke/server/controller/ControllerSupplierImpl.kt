package edu.myrza.archke.server.controller

import edu.myrza.archke.server.command.Command

class ControllerSupplierImpl(private val commands: List<Command>) : ControllerSupplier {

    override fun get(): Controller = ControllerImpl(commands)

}
