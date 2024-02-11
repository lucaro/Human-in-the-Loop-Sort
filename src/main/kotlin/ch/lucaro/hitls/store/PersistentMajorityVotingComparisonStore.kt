package ch.lucaro.hitls.store

import ch.lucaro.hitls.container.ComparisonContainer
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PersistentMajorityVotingComparisonStore(
    minVotesToAcceptOption: Int,
    persistentFile: File,
    containers: Collection<ComparisonContainer<String>>
) :
    MajorityVotingComparisonStore<String, String>(minVotesToAcceptOption) {

    private val itemMap = containers.associateBy { it.item }
    private val idMap = containers.associateBy { it.id }

    private val logger: KLogger = KotlinLogging.logger {}

    val voteCount: Int
        get() = this.votes.size

    val comparisonCount: Int
        get() = this.acceptedPairs.size

    init {

        if (persistentFile.exists()) {
            persistentFile.readLines(Charsets.UTF_8).forEach {
                val s = it.split("\t")
                if (s.size < 3) {
                    return@forEach
                }

                when (s.first()) {
                    "s" -> super.store(itemMap[s[1]]?.id!!, itemMap[s[2]]?.id!!)
                    "v" -> super.vote(s[1], itemMap[s[2]]?.id!!, itemMap[s[3]]?.id!!)
                    "b" -> super.blacklist(itemMap[s[1]]?.id!!, itemMap[s[2]]?.id!!)
                }
            }
        }

    }

    private val writer = PrintWriter(FileWriter(persistentFile, true))

    override fun store(o1: UUID, o2: UUID) {
        super.store(o1, o2)
        synchronized(this.writer) {
            writer.println("s\t${idMap[o1]?.item}\t${idMap[o2]?.item}")
            writer.flush()
        }
    }

    override fun vote(id: String, o1: UUID, o2: UUID) {

        val pair = o1 to o2

        if (acceptedPairs.contains(pair)) {
            return
        }

        synchronized(this.writer) {
            writer.println("v\t${id}\t${idMap[o1]?.item}\t${idMap[o2]?.item}")
        }

        if (votes.containsKey(pair)) {
            votes[pair]!!.add(id)

            logger.debug { "registering vote by $id for ($o1, $o2)" }

            if (votes[pair]!!.size >= this.minVotesToAcceptOption) {
                acceptedPairs.add(pair)
                synchronized(this.writer) {
                    writer.println("s\t${idMap[o1]?.item}\t${idMap[o2]?.item}")
                    writer.flush()
                }
                logger.debug { "sufficient votes for ($o1, $o2), accepting" }
            }

        } else {
            val set = ConcurrentHashMap.newKeySet<String>()
            set.add(id)
            votes[pair] = set
            logger.debug { "registering initial vote by $id for ($o1, $o2)" }
        }


    }

    override fun blacklist(o1: UUID, o2: UUID) {
        super.blacklist(o1, o2)
        synchronized(this.writer) {
            this.writer.println("b\t${idMap[o1]?.item}\t${idMap[o2]?.item}")
            writer.flush()
        }
    }

    fun flush() {
        synchronized(this.writer) {
            writer.flush()
        }
    }
}