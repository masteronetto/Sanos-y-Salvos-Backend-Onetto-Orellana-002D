package com.sanosysalvos.common

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.util.Date

class JwtTokenService(
    private val secret: String,
    private val accessTokenTtlMinutes: Long = 60,
    private val refreshTokenTtlMinutes: Long = 60L * 24 * 7,
) {
    private val signingKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun generateAccessToken(subject: String, claims: Map<String, Any> = emptyMap()): String = generateToken(
        subject = subject,
        ttlMinutes = accessTokenTtlMinutes,
        claims = claims + mapOf("tokenType" to "access"),
    )

    fun generateRefreshToken(subject: String, claims: Map<String, Any> = emptyMap()): String = generateToken(
        subject = subject,
        ttlMinutes = refreshTokenTtlMinutes,
        claims = claims + mapOf("tokenType" to "refresh"),
    )

    fun extractSubject(token: String): String? = parseClaims(token).subject

    fun extractClaims(token: String): Claims = parseClaims(token)

    fun isValid(token: String): Boolean = try {
        parseClaims(token)
        true
    } catch (_: Exception) {
        false
    }

    private fun generateToken(subject: String, ttlMinutes: Long, claims: Map<String, Any>): String {
        val now = Date()
        val expiry = Date(now.time + ttlMinutes * 60_000)

        return Jwts.builder()
            .subject(subject)
            .issuedAt(now)
            .expiration(expiry)
            .claims(claims)
            .signWith(signingKey)
            .compact()
    }

    private fun parseClaims(token: String): Claims = Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .payload
}