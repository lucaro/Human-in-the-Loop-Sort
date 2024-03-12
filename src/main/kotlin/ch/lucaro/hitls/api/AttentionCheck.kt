package ch.lucaro.hitls.api

import ch.lucaro.hitls.SortJob
import ch.lucaro.hitls.container.ComparisonContainer
import ch.lucaro.hitls.store.BasicComparisonStore
import java.util.*
import kotlin.random.Random


class AttentionCheck() {
    companion object {
        val masterList =
            listOf(ComparisonContainer("3.jpg"), ComparisonContainer("2.jpg"), ComparisonContainer("1.jpg"))
    }

    private val store = BasicComparisonStore<String>()

    private val random = Random(System.currentTimeMillis())

    private val sortJob = SortJob(
        random,
        masterList.shuffled(random),
        store
    )


    var complete = false
        private set

    var succeeded = false
        private set


    fun next(): Pair<ComparisonContainer<String>, ComparisonContainer<String>>{
        val next = sortJob.somePair()
        checkComplete()
        return next
    }


    fun store(o1: UUID, o2: UUID) {
        this.store.store(o1, o2)
        checkComplete()
    }

    private fun checkComplete() {
        val sorted = this.sortJob.sorted()
        if (sorted != null) {
            this.complete = true
            this.succeeded = sorted.mapIndexed { index, comparisonContainer -> comparisonContainer.id == masterList[index].id }.all { it }
        }
    }

}