package ch.lucaro.hitls

import java.util.UUID

data class ComparisonContainer<T>(
    val item: T,
    val id: UUID = UUID.randomUUID()
)
