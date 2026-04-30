package com.sanosysalvos.user.service

import com.sanosysalvos.common.ConflictServiceException
import com.sanosysalvos.common.JwtTokenService
import com.sanosysalvos.common.NotFoundServiceException
import com.sanosysalvos.common.UnauthorizedServiceException
import com.sanosysalvos.contracts.AuthResponse
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserProfile
import com.sanosysalvos.contracts.UserRegistrationRequest
import com.sanosysalvos.contracts.UserRole
import com.sanosysalvos.contracts.UserUpdateRequest
import com.sanosysalvos.user.domain.UserAccountEntity
import com.sanosysalvos.user.domain.UserAccountRepository
import com.sanosysalvos.user.domain.UserEventEntity
import com.sanosysalvos.user.domain.UserEventRepository
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserAccountService(
    private val userAccountRepository: UserAccountRepository,
    private val userEventRepository: UserEventRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenService: JwtTokenService,
) {
    fun register(request: UserRegistrationRequest): AuthResponse = createAccount(
        fullName = request.fullName,
        email = request.email,
        phone = request.phone,
        password = request.password,
        role = UserRole.USER,
    )

    fun create(request: UserRegistrationRequest): UserProfile = toProfile(
        createAccount(
            fullName = request.fullName,
            email = request.email,
            phone = request.phone,
            password = request.password,
            role = UserRole.USER,
        ).userId,
    )

    fun login(request: UserLoginRequest): AuthResponse {
        val account = userAccountRepository.findByEmail(request.email)
            .orElseThrow { NotFoundServiceException("User not found") }

        if (!passwordEncoder.matches(request.password, account.passwordHash)) {
            throw UnauthorizedServiceException("Invalid credentials")
        }

        val accessToken = jwtTokenService.generateAccessToken(
            subject = account.id ?: error("User id missing"),
            claims = mapOf("email" to account.email, "role" to account.role.name),
        )
        val refreshToken = jwtTokenService.generateRefreshToken(
            subject = account.id ?: error("User id missing"),
            claims = mapOf("email" to account.email, "role" to account.role.name),
        )

        account.refreshToken = refreshToken
        userAccountRepository.save(account)
        recordEvent(account.id!!, "LOGIN", account.email)

        return AuthResponse(userId = account.id!!, role = account.role, token = accessToken)
    }

    fun refresh(refreshToken: String): AuthResponse {
        val account = userAccountRepository.findByRefreshToken(refreshToken)
            .orElseThrow { UnauthorizedServiceException("Invalid refresh token") }

        if (!jwtTokenService.isValid(refreshToken)) {
            throw UnauthorizedServiceException("Expired refresh token")
        }

        val token = jwtTokenService.generateAccessToken(
            subject = account.id!!,
            claims = mapOf("email" to account.email, "role" to account.role.name),
        )

        return AuthResponse(userId = account.id!!, role = account.role, token = token)
    }

    fun logout(authentication: Authentication): String {
        val account = currentAccount(authentication)
        account.refreshToken = null
        userAccountRepository.save(account)
        recordEvent(account.id!!, "LOGOUT", account.email)
        return account.id!!
    }

    fun me(authentication: Authentication): UserProfile = toProfile(currentAccount(authentication))

    fun getById(id: String): UserProfile = toProfile(findAccount(id))

    fun listUsers(): List<UserProfile> = userAccountRepository.findAll().map { toProfile(it) }

    fun update(id: String, request: UserUpdateRequest): UserProfile {
        val account = findAccount(id)
        account.fullName = request.fullName
        account.email = request.email
        account.phone = request.phone
        request.password?.let { account.passwordHash = passwordEncoder.encode(it) }
        request.role?.let { account.role = it }
        userAccountRepository.save(account)
        recordEvent(account.id!!, "UPDATE", account.email)
        return toProfile(account)
    }

    fun delete(id: String): String {
        val account = findAccount(id)
        userAccountRepository.delete(account)
        recordEvent(id, "DELETE", account.email)
        return id
    }

    fun assignRole(id: String, role: UserRole): UserProfile {
        val account = findAccount(id)
        account.role = role
        userAccountRepository.save(account)
        recordEvent(account.id!!, "ROLE_ASSIGNED", role.name)
        return toProfile(account)
    }

    fun requestResetLink(email: String): String {
        val account = userAccountRepository.findByEmail(email)
            .orElseThrow { NotFoundServiceException("User not found") }
        val resetToken = jwtTokenService.generateRefreshToken(
            subject = account.id!!,
            claims = mapOf("purpose" to "reset", "email" to account.email),
        )
        account.resetToken = resetToken
        account.resetTokenExpiresAt = Instant.now().plusSeconds(60 * 60)
        userAccountRepository.save(account)
        recordEvent(account.id!!, "RESET_REQUESTED", account.email)
        return resetToken
    }

    fun updatePassword(userId: String, token: String, newPassword: String): String {
        val account = findAccount(userId)
        if (account.resetToken != token || account.resetTokenExpiresAt?.isBefore(Instant.now()) == true) {
            throw UnauthorizedServiceException("Invalid or expired reset token")
        }

        account.passwordHash = passwordEncoder.encode(newPassword)
        account.resetToken = null
        account.resetTokenExpiresAt = null
        userAccountRepository.save(account)
        recordEvent(account.id!!, "PASSWORD_UPDATED", account.email)
        return account.id!!
    }

    fun magicLinkLogin(token: String): AuthResponse {
        if (!jwtTokenService.isValid(token)) {
            throw UnauthorizedServiceException("Invalid magic link token")
        }
        val userId = jwtTokenService.extractSubject(token)
            ?: throw UnauthorizedServiceException("Missing subject")
        val account = findAccount(userId)
        val accessToken = jwtTokenService.generateAccessToken(
            subject = account.id!!,
            claims = mapOf("email" to account.email, "role" to account.role.name),
        )
        recordEvent(account.id!!, "MAGIC_LINK_LOGIN", account.email)
        return AuthResponse(userId = account.id!!, role = account.role, token = accessToken)
    }

    fun sendWelcomeEmail(userId: String, email: String): String {
        recordEvent(userId, "WELCOME_EMAIL_SENT", email)
        return email
    }

    fun myEvents(authentication: Authentication): List<String> = userEventRepository
        .findAllByUserIdOrderByCreatedAtDesc(currentAccount(authentication).id!!)
        .map { it.eventType }

    fun registerDeviceToken(requestUserId: String, deviceToken: String): String {
        val account = findAccount(requestUserId)
        account.deviceToken = deviceToken
        userAccountRepository.save(account)
        recordEvent(account.id!!, "DEVICE_TOKEN_REGISTERED", deviceToken)
        return deviceToken
    }

    private fun createAccount(
        fullName: String,
        email: String,
        phone: String?,
        password: String,
        role: UserRole,
    ): AuthResponse {
        if (userAccountRepository.findByEmail(email).isPresent) {
            throw ConflictServiceException("User already exists")
        }

        val account = userAccountRepository.save(
            UserAccountEntity(
                fullName = fullName,
                email = email,
                phone = phone,
                passwordHash = passwordEncoder.encode(password),
                role = role,
            ),
        )
        recordEvent(account.id!!, "REGISTER", email)

        val accessToken = jwtTokenService.generateAccessToken(
            subject = account.id!!,
            claims = mapOf("email" to account.email, "role" to account.role.name),
        )
        val refreshToken = jwtTokenService.generateRefreshToken(
            subject = account.id!!,
            claims = mapOf("email" to account.email, "role" to account.role.name),
        )
        account.refreshToken = refreshToken
        userAccountRepository.save(account)

        return AuthResponse(userId = account.id!!, role = account.role, token = accessToken)
    }

    private fun currentAccount(authentication: Authentication): UserAccountEntity {
        val userId = authentication.name
        return findAccount(userId)
    }

    private fun findAccount(id: String): UserAccountEntity = userAccountRepository.findById(id)
        .orElseThrow { NotFoundServiceException("User not found") }

    private fun toProfile(account: UserAccountEntity): UserProfile = UserProfile(
        id = account.id!!,
        fullName = account.fullName,
        email = account.email,
        phone = account.phone,
        role = account.role,
    )

    private fun toProfile(id: String): UserProfile = toProfile(findAccount(id))

    private fun recordEvent(userId: String, type: String, details: String) {
        userEventRepository.save(
            UserEventEntity(
                userId = userId,
                eventType = type,
                details = details,
            ),
        )
    }
}