package edu.myrza.archke.client.exception

sealed class ClientException(msg: String): RuntimeException(msg) {

    class ConnectionClosed : ClientException("Connection closed")

}
