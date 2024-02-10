package ch.lucaro.hitls

import ch.lucaro.hitls.container.ComparisonContainer
import ch.lucaro.hitls.store.BasicComparisonStore
import ch.lucaro.hitls.store.MajorityVotingComparisonStore
import java.io.File
import kotlin.random.Random

object Simulation {

    @JvmStatic
    fun main(args: Array<String>) {

        for (agreement in 100 downTo 50 step 5) {

            val writer = File("simulations/2_uncertainty/agreement_${agreement.toString().padStart(3, '0')}.tsv").printWriter()

            writer.println("length\trun\tcomparisons\tblacklistings")

            for (length in 8..1024 step 8) {
                for (run in 1..10) {

                    val random = Random(length * run * agreement)
                    val masterList = List(length) { ComparisonContainer(it) }.shuffled(random)
                    val store = MajorityVotingComparisonStore<Int, Int>(3)

                    val job = SortJob(random, masterList, store)

                    var comparisons = 0

                    while (true) {

                        val pair = job.nextPair() ?: break

                        val decision = pair.first.item <= pair.second.item

                        val flip = random.nextInt(100) > agreement

                        if (decision xor flip) {
                            store.vote(comparisons, pair.first.id, pair.second.id)
                        } else {
                            store.vote(comparisons, pair.second.id, pair.first.id)
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