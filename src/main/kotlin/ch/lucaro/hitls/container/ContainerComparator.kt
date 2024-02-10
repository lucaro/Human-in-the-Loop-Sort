package ch.lucaro.hitls.container

import ch.lucaro.hitls.store.ComparisonStore


class ContainerComparator<T>(private val store: ComparisonStore<T>) : Comparator<ComparisonContainer<T>> {

    lateinit var lastComparison: Pair<ComparisonContainer<T>, ComparisonContainer<T>>

    override fun compare(o1: ComparisonContainer<T>, o2: ComparisonContainer<T>): Int {

        lastComparison = o1 to o2

        return store.compare(o1, o2) ?: throw ComparisonUnknownException(o1, o2)
    }

}