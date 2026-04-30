package com.sanosysalvos.user.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserAccountRepository : JpaRepository<UserAccountEntity, String> {
    fun findByEmail(email: String): Optional<UserAccountEntity>
    fun findByRefreshToken(refreshToken: String): Optional<UserAccountEntity>
    fun findByResetToken(resetToken: String): Optional<UserAccountEntity>
}

interface UserEventRepository : JpaRepository<UserEventEntity, String> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: String): List<UserEventEntity>
}