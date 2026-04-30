package com.sanosysalvos.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "user_events")
class UserEventEntity(
    @Id
    var id: String? = null,

    @Column(nullable = false)
    var userId: String,

    @Column(nullable = false)
    var eventType: String,

    @Column(nullable = false)
    var details: String = "",

    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),
) {
    @jakarta.persistence.PrePersist
    fun onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString()
        }
        createdAt = Instant.now()
    }
}