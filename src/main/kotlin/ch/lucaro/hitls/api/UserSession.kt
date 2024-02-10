package ch.lucaro.hitls.api

import ch.lucaro.hitls.container.ComparisonContainer
import java.util.UUID

class UserSession(val sessionId: String) {

    val pageState = PageState()

    private var remaining = 10

    var sortJobName: String? = null
        private set

    val taskStarted : Boolean
        get() = sortJobName != null

    private var nextPair: Pair<ComparisonContainer<String>, ComparisonContainer<String>>? = null

    private val seenOptions = HashSet<Triple<String, UUID, UUID>>()


    fun start() {
       this.sortJobName = API.jobManager.nextJobName()
       pageState.page = PageState.Page.COMPARE
    }

    fun next(): Pair<ComparisonContainer<String>, ComparisonContainer<String>> {

        if (nextPair != null) {
            return nextPair!!
        }

        val job = API.jobManager.getJob(sortJobName!!)!!

        if (!job.complete) {
            this.nextPair = job.somePair()
        } else {
            sortJobName = API.jobManager.nextJobName()
            val job2 = API.jobManager.getJob(sortJobName!!)!!
            this.nextPair = job2.somePair()
        }

        val seen = Triple(sortJobName!!, this.nextPair!!.first.id, this.nextPair!!.second.id)

        if (this.seenOptions.contains(seen)) { //duplicate, replace with random
            this.nextPair = API.jobManager.getJob(sortJobName!!)!!.randomPair()
            val seen2 = Triple(sortJobName!!, this.nextPair!!.first.id, this.nextPair!!.second.id)
            this.seenOptions.add(seen2)
        }

        this.seenOptions.add(seen)

        return nextPair!!
    }

    fun vote(o1: UUID, o2: UUID) {
        if (nextPair == null) {
            return //prevent duplicates
        }
        API.jobManager.getStore(this.sortJobName!!)?.vote(sessionId, o1, o2)
        nextPair = null
        if (--remaining <= 0) {
            pageState.page = PageState.Page.DONE
        }
    }

}