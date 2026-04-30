package com.sanosysalvos.match.domain

import org.springframework.data.jpa.repository.JpaRepository

interface MatchRepository : JpaRepository<MatchEntity, String> {
    fun findAllByUserId(userId: String): List<MatchEntity>
    fun findAllByNotifiedFalse(): List<MatchEntity>
    fun findAllByStatus(status: MatchStatus): List<MatchEntity>
}