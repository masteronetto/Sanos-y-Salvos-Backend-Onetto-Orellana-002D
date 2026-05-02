package com.sanosysalvos.contracts

enum class UserRole {
    USER,
    ADMIN,
    COLLABORATOR,
}

data class UserRegistrationRequest(
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val password: String,
)

data class UserLoginRequest(
    val email: String,
    val password: String,
)

data class AuthResponse(
    val userId: String,
    val role: UserRole,
    val token: String,
)

data class DeviceTokenRequest(
    val userId: String,
    val deviceToken: String,
)

data class UserProfile(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val role: UserRole = UserRole.USER,
)

data class UserUpdateRequest(
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val password: String? = null,
    val role: UserRole? = null,
)
