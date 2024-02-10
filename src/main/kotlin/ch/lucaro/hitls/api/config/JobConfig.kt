package ch.lucaro.hitls.api.config

import kotlinx.serialization.Serializable

@Serializable
data class JobConfig(val name: String, val imageFolder: String, val votes: Int = 3)
