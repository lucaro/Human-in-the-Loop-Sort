package ch.lucaro.hitls.store

import ch.lucaro.hitls.container.ComparisonContainer
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*

class BasicComparisonStore<T> : ComparisonStore<T> {

    private val logger: KLogger = KotlinLogging.logger {}

    private val pairs = mutableSetOf<Pair<UUID, UUID>>()

    override fun compare(o1: ComparisonContainer<T>, o2: ComparisonContainer<T>): Int? {
        //check for known matches
        if (pairs.contains(o1.id to o2.id)) {
            logger.debug { "found ($o1, $o2)" }
            return -1
        }
        if (pairs.contains(o2.id to o1.id)) {
            logger.debug { "found ($o2, $o1)" }
            return 1
        }

        //check for transitive matches
        val candidates = pairs.filter { it.first == o1.id || it.second == o1.id || it.first == o2.id || it.second == o2.id }

        //o1 < center < o2
        candidates.asSequence().filter { it.first == o1.id }.map { it.second }.forEach { center ->
            if(candidates.any { it.first == center && it.second == o2.id }) {
                logger.debug { "found ($o1, $o2) transitively" }
                store(o1.id, o2.id)
                return -1
            }
        }

        //o2 < center < o1
        candidates.asSequence().filter { it.first == o2.id }.map { it.second }.forEach { center ->
            if(candidates.any { it.first == center && it.second == o1.id }) {
                logger.debug { "found ($o2, $o1) transitively" }
                store(o2.id, o1.id)
                return 1
            }
        }

        //no match found
        return null
    }

    override fun store(o1: UUID, o2: UUID) {
        val pair = o1 to o2
        if (pairs.contains(pair)) {
            return
        }
        logger.debug { "storing ($o1, $o2)" }
        pairs.add(pair)
    }

    override fun toString(): String {
        return "store with ${pairs.size} elements"
    }

}