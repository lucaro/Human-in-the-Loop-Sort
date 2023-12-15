package ch.lucaro.hitls.container

class ComparisonUnknownException(o1: ComparisonContainer<*>, o2: ComparisonContainer<*>) : Exception("Comparison between $o1 and $o2 is not yet known") {

    val pair = o1 to o2

}