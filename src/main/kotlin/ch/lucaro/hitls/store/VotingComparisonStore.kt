package ch.lucaro.hitls.store

interface VotingComparisonStore<T, ID> : ComparisonStore<T> {

    /**
     * Stores the information that id considers o1 to be smaller than o2.
     */
    fun vote(id: ID, o1: T, o2: T)

}