package us.maxpowa.ircclient.connection

data class Credentials(
        val host: Host,
        val nick: String,
        val password: String
)