package edu.myrza.archke

import edu.myrza.archke.server.Archke
import edu.myrza.archke.server.config.ArchkeConfig

fun main(args: Array<String>) {
    val archke = Archke(
        ArchkeConfig(port = 9999)
    )
    archke.start()
}
