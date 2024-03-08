package ch.lucaro.hitls.api

import ch.lucaro.hitls.api.config.Config
import java.util.concurrent.ConcurrentHashMap

object UserSessionManager {

    private val sessions = ConcurrentHashMap<String, UserSession>()

    lateinit var config: Config

    operator fun get(key: String): UserSession {

        val existing = sessions[key]
        if (existing != null) {
            return existing
        } else {
            val newSession = UserSession(key, config.comparisonsPerUser)
            sessions[key] = newSession
            return newSession
        }

    }

    fun allSessions() = sessions.values.sortedByDescending { it.remaining }

}