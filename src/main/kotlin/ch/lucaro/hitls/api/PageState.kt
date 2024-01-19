package ch.lucaro.hitls.api

class PageState {

    enum class Page {
        START,
        COMPARE,
        DONE
    }

    var page: Page = Page.START
        internal set

    val noConsentLink = "http://google.com"


}