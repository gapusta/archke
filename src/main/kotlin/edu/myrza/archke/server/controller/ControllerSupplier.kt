package edu.myrza.archke.server.controller

/*
*  Controller factory class
* */
interface ControllerSupplier {

    fun get(): Controller

}
