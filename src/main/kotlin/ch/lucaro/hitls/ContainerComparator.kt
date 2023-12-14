package ch.lucaro.hitls


class ContainerComparator<T>(private val store: ComparisonStore<T>) : Comparator<ComparisonContainer<T>> {
    override fun compare(o1: ComparisonContainer<T>, o2: ComparisonContainer<T>): Int =
        store.compare(o1.item, o2.item) ?: throw ComparisonUnknownException(o1, o2)
}