package ch.lucaro.hitls.api

import ch.lucaro.hitls.SortJob

class UserSession(val sessionId: String) {

    val pageState = PageState()

    private var sortJob: SortJob<*>? = null

    val taskStarted : Boolean
        get() = sortJob != null

    fun startJob(job: SortJob<*>) {
        this.sortJob = job
        pageState.page = PageState.Page.COMPARE
    }

}