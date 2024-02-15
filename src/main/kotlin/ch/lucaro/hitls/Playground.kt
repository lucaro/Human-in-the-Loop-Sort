package ch.lucaro.hitls

import ch.lucaro.hitls.api.API
import ch.lucaro.hitls.api.config.Config
import ch.lucaro.hitls.api.config.JobConfig
import ch.lucaro.hitls.container.ComparisonContainer
import ch.lucaro.hitls.store.BasicComparisonStore
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.random.Random

object Playground {

    private val random = Random(0)

    private val masterList = List(8) { ComparisonContainer(it) }.shuffled(random)

    private val store = BasicComparisonStore<Int>()

    private val job = SortJob(
        random, masterList, store
    )

    @JvmStatic
    fun main(args: Array<String>) {

//        var counter = 0
//
//        while (true) {
//
//            val pair = job.nextPair() ?: break
//
//            println(pair)
//            val response = readln()
//
//            if (response.isEmpty()) {
//                store.store(pair.first, pair.second)
//            } else {
//                store.store(pair.second, pair.first)
//            }
//            ++counter
//
//        }
//
//        val list = job.sorted()!!
//        println(list.map { it.item })
//
//        println("comparisons: $counter")
//        println("expected comparisons: ${ceil((masterList.size * log2(masterList.size.toFloat())).toDouble()).toInt()}")
//        println(store)
//        println("total possible combinations: ${(masterList.size * (masterList.size - 1)) / 2}")


        API.init(Config(port = 8080, jobs = listOf(
            JobConfig("visualGenome", "imagesets/visual_genome_selection"),
            JobConfig("YFCC100M", "imagesets/yfcc100m_selection"),
        )))

        while (readln() != "quit") {

        }

        API.stop()


    }

}