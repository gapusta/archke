package edu.myrza.archke

import edu.myrza.archke.server.Archke
import edu.myrza.archke.server.config.ArchkeConfig

fun main(args: Array<String>) {
    val config = ArchkeConfig(port = 9999)
    val archke = Archke(config)
    archke.start()
}
