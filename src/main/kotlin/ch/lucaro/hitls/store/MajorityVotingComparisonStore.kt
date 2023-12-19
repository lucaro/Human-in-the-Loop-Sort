package ch.lucaro.hitls.store

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

class MajorityVotingComparisonStore<T, ID>(

    private val minVotesToAcceptOption: Int

) : VotingComparisonStore<T, ID> {

    private val logger: KLogger = KotlinLogging.logger {}

    private val votes = ConcurrentHashMap<Pair<T, T>, MutableSet<ID>>()

    private val acceptedPairs = ConcurrentHashMap.newKeySet<Pair<T, T>>()

    private val blackList = ConcurrentHashMap.newKeySet<Pair<T, T>>()


    override fun compare(o1: T, o2: T): Int? {
        //check for known matches
        val p1 = o1 to o2
        if (acceptedPairs.contains(p1)) {
            logger.debug { "found ($o1, $o2)" }
            return -1
        }
        if ((votes[p1]?.size ?: 0) >= this.minVotesToAcceptOption && !blackList.contains(p1)) {
            acceptedPairs.add(p1)
            logger.debug { "sufficient votes for ($o1, $o2), accepting" }
            return -1
        }

        val p2 = o2 to o1
        if (acceptedPairs.contains(p2)) {
            logger.debug { "found ($o2, $o1)" }
            return 1
        }
        if ((votes[p2]?.size ?: 0) >= this.minVotesToAcceptOption && !blackList.contains(p2)) {
            acceptedPairs.add(p2)
            logger.debug { "sufficient votes for ($o2, $o1), accepting" }
            return 1
        }

        //check for transitive matches
        val candidates = acceptedPairs.filter { it.first == o1 || it.second == o1 || it.first == o2 || it.second == o2 }

        //o1 < center < o2
        candidates.asSequence().filter { it.first == o1 }.map { it.second }.forEach { center ->
            if(candidates.any { it.first == center && it.second == o2 }) {
                logger.debug { "found ($o1, $o2) transitively" }
                acceptedPairs.add(p1)
                return -1
            }
        }

        //o2 < center < o1
        candidates.asSequence().filter { it.first == o2 }.map { it.second }.forEach { center ->
            if(candidates.any { it.first == center && it.second == o1 }) {
                logger.debug { "found ($o2, $o1) transitively" }
                acceptedPairs.add(p2)
                return 1
            }
        }

        //no match found
        return null
    }

    override fun store(o1: T, o2: T) {
        val pair = o1 to o2
        if (acceptedPairs.contains(pair)) {
            return
        }

        if (acceptedPairs.contains(o2 to o1)) {
            throw IllegalArgumentException("cannot store $o2 < $o1, since $o1 < $o2 is already stored")
        }

        logger.debug { "storing ($o1, $o2)" }
        acceptedPairs.add(pair)
    }

    override fun vote(id: ID, o1: T, o2: T) {

        val pair = o1 to o2
        if (acceptedPairs.contains(pair)) {
            return
        }

        if (votes.containsKey(pair)) {
            votes[pair]!!.add(id)

            logger.debug { "registering vote by $id for ($o1, $o2)" }

            if (votes[pair]!!.size >= this.minVotesToAcceptOption) {
                acceptedPairs.add(pair)
                logger.debug { "sufficient votes for ($o1, $o2), accepting" }
            }

        } else {
            val set = ConcurrentHashMap.newKeySet<ID>()
            set.add(id)
            votes[pair] = set

            logger.debug { "registering initial vote by $id for ($o1, $o2)" }
        }

    }

    override fun blacklist(o1: Any, o2: Any) {

        logger.info { "blacklisting ($o1, $o2)" }

        val p1 = o1 as T to o2 as T
        if (acceptedPairs.contains(p1)) {
            blackList.add(p1)
            acceptedPairs.remove(p1)
            return
        }
        val p2 = o2 to o1
        if (acceptedPairs.contains(p2)) {
            blackList.add(p2)
            acceptedPairs.remove(p2)
            return
        }
    }
}