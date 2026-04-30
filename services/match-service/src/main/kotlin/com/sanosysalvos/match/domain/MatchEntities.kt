package com.sanosysalvos.match.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

enum class MatchStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
}

@Entity
@Table(name = "matches")
class MatchEntity(
    @Id
    var id: String? = null,
    @Column(nullable = false)
    var userId: String,
    @Column(nullable = false)
    var reportId: String,
    @Column(nullable = false)
    var matchedReportId: String,
    @Column(nullable = false)
    var score: Double,
    @Column(nullable = false, columnDefinition = "text")
    var reason: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: MatchStatus = MatchStatus.PENDING,
    @Column(nullable = false)
    var notified: Boolean = false,
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),
) {
    @PrePersist
    fun onCreate() {
        if (id == null) id = UUID.randomUUID().toString()
        createdAt = Instant.now()
        updatedAt = createdAt
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}