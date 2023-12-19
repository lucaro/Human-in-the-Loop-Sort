package ch.lucaro.hitls

import ch.lucaro.hitls.container.ComparisonContainer
import ch.lucaro.hitls.store.BasicComparisonStore
import ch.lucaro.hitls.store.MajorityVotingComparisonStore
import java.io.File
import kotlin.random.Random

object Simulation {

    @JvmStatic
    fun main(args: Array<String>) {

        for (agreement in 95 downTo 50 step 5) {

            val writer = File("simulations/2_uncertainty/agreement_$agreement.tsv").printWriter()

            writer.println("length\trun\tcomparisons\blacklistings")

            for (length in 16..1024 step 16) {
                for (run in 1..10) {

                    val random = Random(length * run * agreement)
                    val masterList = List(length) { ComparisonContainer(it) }.shuffled(random)
                    val store = MajorityVotingComparisonStore<Int, Int>(3)

                    val job = SortJob(random, masterList, store)

                    var comparisons = 0

                    while (true) {

                        val pair = job.nextPair() ?: break

                        val decision = pair.first <= pair.second

                        if (decision xor (random.nextInt(100) <= agreement)) {
                            store.vote(comparisons, pair.first, pair.second)
                        } else {
                            store.vote(comparisons, pair.second, pair.first)
                        }

                        ++comparisons

                    }

                    writer.println("$length\t$run\t$comparisons\t${job.blacklistCount}")

                }

                println(length)

            }

            writer.flush()
            writer.close()

        }
    }

}