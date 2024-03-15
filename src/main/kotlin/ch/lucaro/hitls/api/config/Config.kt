package ch.lucaro.hitls.api.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Config(
    val port: Int = 8080,
    val sslPort: Int = 8443,
    val comparisonsPerUser: Int = 50,
    val noConsentLink: String = "http://google.com", //TODO
    val successLink: String = "http://google.com", //TODO
    val checkFailedLink: String = "http://google.com", //TODO
    val statusSecret: String = "secret",
    val jobs: List<JobConfig> = emptyList()
) {

    companion object{

        fun load(file: File): Config = Json.decodeFromString<Config>(file.readText())

    }

}
