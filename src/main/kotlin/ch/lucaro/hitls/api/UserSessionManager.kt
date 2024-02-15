package ch.lucaro.hitls.api

import java.util.concurrent.ConcurrentHashMap

object UserSessionManager {

    private val sessions = ConcurrentHashMap<String, UserSession>()

    operator fun get(key: String): UserSession {

        val existing = sessions[key]
        if (existing != null) {
            return existing
        } else {
            val newSession = UserSession(key)
            sessions[key] = newSession
            return newSession
        }

    }

    fun allSessions() = sessions.values.sortedByDescending { it.remaining }

}