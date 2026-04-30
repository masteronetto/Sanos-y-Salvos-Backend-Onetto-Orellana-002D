package com.sanosysalvos.user.domain

import com.sanosysalvos.contracts.UserRole
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

@Entity
@Table(name = "user_accounts")
class UserAccountEntity(
    @Id
    var id: String? = null,

    @Column(nullable = false)
    var fullName: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column
    var phone: String? = null,

    @Column(nullable = false)
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column
    var deviceToken: String? = null,

    @Column
    var refreshToken: String? = null,

    @Column
    var resetToken: String? = null,

    @Column
    var resetTokenExpiresAt: Instant? = null,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),
) {
    @PrePersist
    fun onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString()
        }
        createdAt = Instant.now()
        updatedAt = createdAt
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}