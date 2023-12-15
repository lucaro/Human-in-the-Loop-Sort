package ch.lucaro.hitls.store

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

class BasicComparisonStore<T> : ComparisonStore<T> {

    private val logger: KLogger = KotlinLogging.logger {}

    private val pairs = mutableSetOf<Pair<T, T>>()

    override fun compare(o1: T, o2: T): Int? {
        //check for known matches
        if (pairs.contains(o1 to o2)) {
            logger.debug { "found ($o1, $o2)" }
            return -1
        }
        if (pairs.contains(o2 to o1)) {
            logger.debug { "found ($o2, $o1)" }
            return 1
        }

        //check for transitive matches
        val candidates = pairs.filter { it.first == o1 || it.second == o1 || it.first == o2 || it.second == o2 }

        //o1 < center < o2
        candidates.asSequence().filter { it.first == o1 }.map { it.second }.forEach { center ->
            if(candidates.any { it.first == center && it.second == o2 }) {
                logger.debug { "found ($o1, $o2) transitively" }
                store(o1, o2)
                return -1
            }
        }

        //o2 < center < o1
        candidates.asSequence().filter { it.first == o2 }.map { it.second }.forEach { center ->
            if(candidates.any { it.first == center && it.second == o1 }) {
                logger.debug { "found ($o2, $o1) transitively" }
                store(o2, o1)
                return 1
            }
        }

        //no match found
        return null
    }

    override fun store(o1: T, o2: T) {
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