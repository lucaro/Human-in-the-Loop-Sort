package ch.lucaro.hitls

import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random

object Playground {

    private val random = Random(0)

    private val masterList = List(15) { ComparisonContainer(it) }.shuffled(random)

    private val store = object : ComparisonStore<Int> {

        private val pairs = mutableSetOf<Pair<Int, Int>>()

        override fun compare(o1: Int, o2: Int): Int? {
            //check for known matches
            if (pairs.contains(o1 to o2)) {
                println("found ($o1, $o2)")
                return -1
            }
            if (pairs.contains(o2 to o1)) {
                println("found ($o2, $o1)")
                return 1
            }

            //check for transitive matches
            val candidates = pairs.filter { it.first == o1 || it.second == o1 || it.first == o2 || it.second == o2 }

            //o1 < center < o2
            candidates.asSequence().filter { it.first == o1 }.map { it.second }.forEach { center ->
                if(candidates.any { it.first == center && it.second == o2 }) {
                    println("found ($o1, $o2) transitively")
                    store(o1, o2)
                    return -1
                }
            }

            //o2 < center < o1
            candidates.asSequence().filter { it.first == o2 }.map { it.second }.forEach { center ->
                if(candidates.any { it.first == center && it.second == o1 }) {
                    println("found ($o2, $o1) transitively")
                    store(o2, o1)
                    return 1
                }
            }


            //no match found
            return null
        }

        override fun store(o1: Int, o2: Int) {
            val pair = o1 to o2
            if (pairs.contains(pair)) {
                return
            }
            println("storing ($o1, $o2)")
            pairs.add(pair)
        }

        override fun toString(): String {
            return "store with ${pairs.size} elements"
        }
    }

    private val comparator = ContainerComparator<Int>(store)

    private fun nextPair(): Pair<Int, Int>? {

        try {

            for (i in 5 downTo 0) {

                val sublistLength = max(2, masterList.size / 2.0.pow(i).toInt())

                val list = if (sublistLength >= masterList.size) {
                    masterList
                } else {
                    val start = random.nextInt(masterList.size - sublistLength - 1)
                    masterList.subList(start, start + sublistLength)
                }

                val sorted = list.sortedWith(
                    comparator
                )

                //since sort of sublist succeeded, we can infer additional relations
                for (start in sorted.indices) {
                    for(end in start + 1..< sorted.size) {
                        store.store(sorted[start].item, sorted[end].item)
                    }
                }

            }
        } catch (e: ComparisonUnknownException) {
            val pair = e.pair
            return pair.first.item as Int to pair.second.item as Int
        }

        return null

    }

    @JvmStatic
    fun main(args: Array<String>) {

        var counter = 0

        while (true) {

            val pair = nextPair() ?: break

            println(pair)
            val response = readln()

            if (response.isEmpty()) {
                store.store(pair.first, pair.second)
            } else {
                store.store(pair.second, pair.first)
            }
            ++counter

        }

        val list = masterList.sortedWith(comparator)
        println(list.map { it.item })

        println("comparisons: $counter")
        println(store)
        println("total possible combinations: ${(masterList.size * (masterList.size - 1)) / 2}")


    }

}