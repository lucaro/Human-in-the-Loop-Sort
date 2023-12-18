package ch.lucaro.hitls

import ch.lucaro.hitls.container.ComparisonContainer
import ch.lucaro.hitls.store.BasicComparisonStore
import java.io.File
import kotlin.random.Random

object Simulation {

    @JvmStatic
    fun main(args: Array<String>) {

        val writer = File("simulations/1_no_uncertainty/sublist.tsv").printWriter()

        writer.println("length\trun\tcomparisons")

        for(length in 8 .. 1024) {
            for (run in 1 .. 10) {

                val random = Random(length * run)
                val masterList = List(length) { ComparisonContainer(it) }.shuffled(random)
                val store = BasicComparisonStore<Int>()

                val job = SortJob( random, masterList, store )

                var comparisons = 0

                while (true) {

                    val pair = job.nextPair() ?: break

                    //no uncertainty
                    if (pair.first <= pair.second) {
                        store.store(pair.first, pair.second)
                    } else {
                        store.store(pair.second, pair.first)
                    }

                    ++comparisons

                }

                writer.println("$length\t$run\t$comparisons")

            }

            println(length)

        }

        writer.flush()
        writer.close()

    }

}