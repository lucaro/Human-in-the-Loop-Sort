package ch.lucaro.hitls

import ch.lucaro.hitls.api.API
import ch.lucaro.hitls.api.config.Config
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File

object Main {

    private val logger: KLogger = KotlinLogging.logger {}
    @JvmStatic
    fun main(args: Array<String>) {

        val configFile = File(args.firstOrNull() ?: "config.json")

        val config = try{
            val cfg = Config.load(configFile)
            logger.info { "loaded config from ${configFile.absolutePath}" }
            cfg
        } catch (e: Exception) {
            logger.info { "could not load config from ${configFile.absolutePath}" }
            Config()
        }

        API.init(config)

        while (readln() != "quit") {
            println("type 'quit' to exit")
        }

        API.stop()

    }

}