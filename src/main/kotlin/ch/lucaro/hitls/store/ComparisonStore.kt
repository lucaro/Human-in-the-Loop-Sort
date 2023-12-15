package ch.lucaro.hitls.store

/**
 * Stores pairwise comparisons of elements of type <T>
 */
interface ComparisonStore<T> {

    /**
     * Acts as a lookup for a comparator. If the comparison between the inputs is known, it is returned.
     * Otherwise, null is returned.
     */
    fun compare(o1: T, o2: T): Int?

    /**
     * Stores the information about o1 being smaller than o2.
     */
    fun store(o1: T, o2: T)

}