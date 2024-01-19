package ch.lucaro.hitls.api.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Config(
    val port: Int = 8080,
    val jobs: List<JobConfig> = emptyList()
) {

    companion object{

        fun load(file: File): Config = Json.decodeFromString<Config>(file.readText())

    }

}
