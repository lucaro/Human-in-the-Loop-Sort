package ch.lucaro.hitls.api

import ch.lucaro.hitls.container.ComparisonContainer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class UserSession(val sessionId: String, private val totalComparisons: Int) {

    companion object {
        private val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.UK)
    }

    enum class Page {
        START,
        CHECK,
        COMPARE,
        FAILED,
        DONE
    }

    val startTime = System.currentTimeMillis()

    var page: Page = Page.START
        internal set

    val attentionCheck = AttentionCheck()

    var userId: String = "none"
        set(value) {
            if (field == "none") {
                field = value
            }
        }

    var remaining = totalComparisons
        private set

    var sortJobName: String? = null
        private set
        get() = if (page == Page.CHECK) "images/check" else field

    val taskStarted : Boolean
        get() = sortJobName != null

    private var nextPair: Pair<ComparisonContainer<String>, ComparisonContainer<String>>? = null

    private val seenOptions = HashSet<Triple<String, UUID, UUID>>()
    private val votes = HashSet<Triple<String, UUID, UUID>>()
    private val voteTimes = ArrayList<Long>(remaining)

    val voteCount: Int
        get() = votes.size

    val lastVoteTime: Long
        get() = voteTimes.lastOrNull() ?: -1L

    val lastVoteTimeFormatted: String
        get() = simpleDateFormat.format(Date(lastVoteTime))

    val meanVoteTime: Float
        get() = when {
            voteTimes.isEmpty() -> 0f
            voteTimes.size == 1 -> 0f
            else -> (voteTimes.last() - voteTimes.first()).toFloat() / voteTimes.size
        }

    val totalSessionTime: Long
        get() = if (voteTimes.isNotEmpty()) {
            voteTimes.last() - startTime
        } else {
            0
        }

    val progress: Float
        get() = (100 * votes.size).toFloat() / totalComparisons

    fun start() {
       this.sortJobName = API.jobManager.nextJobName()
       page = Page.CHECK
    }

    fun next(): Pair<ComparisonContainer<String>, ComparisonContainer<String>> {

        if (nextPair != null) {
            return nextPair!!
        }

        if (page == Page.CHECK) {
            nextPair = attentionCheck.next()
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

        if (page == Page.CHECK) {
            attentionCheck.store(o1, o2)
            nextPair = null
            return
        }


        val vote = Triple(sortJobName!!, o1, o2)

        if (votes.contains(vote)) {
            return
        }

        votes.add(vote)

        voteTimes.add(System.currentTimeMillis())

        if (nextPair == null) {
            return //prevent duplicates
        }
        API.jobManager.getStore(this.sortJobName!!)?.vote(sessionId, o1, o2)
        nextPair = null
        if (--remaining <= 0) {
            page = Page.DONE
        }
    }

}