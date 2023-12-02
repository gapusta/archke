package edu.myrza.archke.server.controller.dto

import edu.myrza.archke.server.controller.dto.Result.STATUS.*

data class Result(val status: STATUS, val output: ByteArray) {

    enum class STATUS { DONE, NOT_DONE }

    fun done(): Boolean = status == DONE

}
