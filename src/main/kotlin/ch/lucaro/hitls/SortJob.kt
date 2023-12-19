package ch.lucaro.hitls

import ch.lucaro.hitls.container.ComparisonContainer
import ch.lucaro.hitls.container.ComparisonUnknownException
import ch.lucaro.hitls.container.ContainerComparator
import ch.lucaro.hitls.store.ComparisonStore
import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random

class SortJob<T>(
    private val random: Random,
    private val list: List<ComparisonContainer<T>>,
    private val store: ComparisonStore<T>
) {

    private val comparator = ContainerComparator(this.store)

    fun nextPair(): Pair<T, T>? {

        try {

            for (i in 5 downTo 0) {

                val sublistLength = max(2, list.size / 2.0.pow(i).toInt())

                val list = if (sublistLength >= list.size) {
                    list
                } else {
                    val start = random.nextInt(list.size - sublistLength - 1)
                    list.subList(start, start + sublistLength)
                }

                //sort to trigger exception on unknown comparison
                list.sortedWith(comparator)

            }
        } catch (e: ComparisonUnknownException) {
            val pair = e.pair
            return pair.first.item as T to pair.second.item as T
        }

        return null

    }

    fun sorted(): List<ComparisonContainer<T>>? =
        try {
            this.list.sortedWith(comparator)
        } catch (e: Exception) {
            null
        }


}