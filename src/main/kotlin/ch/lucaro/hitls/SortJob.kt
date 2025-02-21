package ch.lucaro.hitls

import ch.lucaro.hitls.container.ComparisonContainer
import ch.lucaro.hitls.container.ComparisonUnknownException
import ch.lucaro.hitls.container.ContainerComparator
import ch.lucaro.hitls.store.ComparisonStore
import ch.lucaro.hitls.store.VotingComparisonStore
import java.lang.Math.pow
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random

class SortJob<T>(
    private val random: Random,
    private val list: List<ComparisonContainer<T>>,
    private val store: ComparisonStore<T>
) {


    private val comparator = ContainerComparator(this.store)

    //start with a sub-list size of 4
    private val startExponent = max(1, log2(list.size.toFloat()).toInt() - 2)

    var sortedList : List<ComparisonContainer<T>>? = null
        private set

    var blacklistCount = 0
        private set

    val elementCount: Int
        get() = list.size

    val complete: Boolean
        get() = sortedList != null


    fun nextPair(): Pair<ComparisonContainer<T>, ComparisonContainer<T>>? {

        try {

            for (i in startExponent downTo 0) {

                val sublistLength = max(2, list.size / (1 shl i))

                val list = if (sublistLength >= list.size) {
                    list
                } else {
                    val start = random.nextInt(list.size - sublistLength)
                    list.subList(start, start + sublistLength)
                }

                //sort to trigger exception on unknown comparison
                list.sortedWith(comparator)

            }

            sortedList = list.sortedWith(comparator)

        } catch (e: ComparisonUnknownException) {
            val pair = e.pair
            return pair.first as ComparisonContainer<T> to pair.second as ComparisonContainer<T>
        } catch (i: java.lang.IllegalArgumentException) {

            val last = comparator.lastComparison

            if (store is VotingComparisonStore<*, *>) {
                store.blacklist(last.first.id, last.second.id)
                ++blacklistCount
            }

        }

        return null

    }

    fun sorted(): List<ComparisonContainer<T>>? =
        try {
            this.list.sortedWith(comparator)
        } catch (e: Exception) {
            null
        }

    fun randomPair(): Pair<ComparisonContainer<T>, ComparisonContainer<T>> {
        val first = list.random()
        var second = list.random()

        while (first == second) {
            second = list.random()
        }

        return first to second
    }

    fun somePair(): Pair<ComparisonContainer<T>, ComparisonContainer<T>> {

        if (!complete) {
            val next = nextPair()
            if (next != null) {
                return next
            }
        }

        return randomPair()

    }



}