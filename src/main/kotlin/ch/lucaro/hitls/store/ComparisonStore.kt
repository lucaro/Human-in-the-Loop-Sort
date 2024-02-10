package ch.lucaro.hitls.store

import ch.lucaro.hitls.container.ComparisonContainer
import java.util.*

/**
 * Stores pairwise comparisons of elements of type <T>
 */
interface ComparisonStore<T> {

    /**
     * Acts as a lookup for a comparator. If the comparison between the inputs is known, it is returned.
     * Otherwise, null is returned.
     */
    fun compare(o1: ComparisonContainer<T>, o2: ComparisonContainer<T>): Int?

    /**
     * Stores the information about o1 being smaller than o2.
     */
    fun store(o1: UUID, o2: UUID)

}