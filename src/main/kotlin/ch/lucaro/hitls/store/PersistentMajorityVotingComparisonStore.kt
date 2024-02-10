package ch.lucaro.hitls.store

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class PersistentMajorityVotingComparisonStore(minVotesToAcceptOption: Int, private val persistentFile: File) :
    MajorityVotingComparisonStore<String, String>(minVotesToAcceptOption) {

    init {

        persistentFile.readLines(Charsets.UTF_8).forEach {
            val s = it.split("\t")
            if (s.size < 3) {
                return@forEach
            }

            when (s.first()) {
                "s" -> super.store(s[1], s[2])
                "v" -> super.vote(s[1], s[2], s[3])
                "b" -> super.blacklist(s[1], s[2])
            }
        }

    }

    private val writer = PrintWriter(FileWriter(persistentFile, true))

    override fun store(o1: String, o2: String) {
        super.store(o1, o2)
        synchronized(this.writer) {
            writer.println("s\t${o1}\t${o2}")
            writer.flush()
        }
    }

    override fun vote(id: String, o1: String, o2: String) {
        super.vote(id, o1, o2)
        synchronized(this.writer) {
            writer.println("v\t${o1}\t${o2}\t${id}")
        }
    }

    override fun blacklist(o1: Any, o2: Any) {
        super.blacklist(o1, o2)
        synchronized(this.writer) {
            this.writer.println("b\t${o1}\t${o2}")
            writer.flush()
        }
    }

    fun flush() {
        synchronized(this.writer) {
            writer.flush()
        }
    }
}