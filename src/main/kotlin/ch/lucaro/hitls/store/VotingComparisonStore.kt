package ch.lucaro.hitls.store

import java.util.UUID

interface VotingComparisonStore<T, ID> : ComparisonStore<T> {

    /**
     * Stores the information that id considers o1 to be smaller than o2.
     */
    fun vote(id: ID, o1: UUID, o2: UUID)

    /**
     * Explicitly blacklists a pairing to remove inconsistencies.
     */
    fun blacklist(o1: UUID, o2: UUID)

}