package ch.lucaro.hitls.container

import java.util.UUID

data class ComparisonContainer<T>(
    val item: T,
    val id: UUID = UUID.randomUUID()
)
