package ch.lucaro.hitls

import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random

object Playground {

    private val random = Random(0)

    private val masterList = List(8) { ComparisonContainer(it) }.shuffled(random)

    private val store = object : ComparisonStore<Int> {

        private val pairs = mutableSetOf<Pair<Int, Int>>()

        override fun compare(o1: Int, o2: Int): Int? {
            if (pairs.contains(o1 to o2)) {
                println("found ($o1, $o2)")
                return -1
            }
            if (pairs.contains(o2 to o1)) {
                println("found ($o2, $o1)")
                return 1
            }
            return null
        }

        override fun store(o1: Int, o2: Int) {
            println("storing ($o1, $o2)")
            pairs.add(o1 to o2)
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

                list.sortedWith(
                    comparator
                )

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
            val response = readln().toInt()

            if (response <= 0) {
                store.store(pair.first, pair.second)
            } else {
                store.store(pair.second, pair.first)
            }
            ++counter

        }

        val list = masterList.sortedWith(comparator)
        println(list.map { it.item })

        println("comparisons: $counter")

        println("total possible combinations: ${(masterList.size * (masterList.size - 1)) / 2}")


    }

}