package edu.myrza.archke.server

enum class Response(val code: Int) {
    OK(1), ERROR(2);

    companion object {

        fun byCode(code: Int): Response {
            return values().find { it.code == code }
                ?: throw IllegalArgumentException("No response with code : $code")
        }

    }

}
