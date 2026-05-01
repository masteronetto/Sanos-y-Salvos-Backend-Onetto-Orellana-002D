package com.sanosysalvos.bff.client

import com.sanosysalvos.bff.config.XanoAuthClientProperties
import com.sanosysalvos.contracts.AuthResponse
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserRegistrationRequest
import com.sanosysalvos.contracts.UserRole
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.util.UUID

@Component
class XanoAuthClient(
    restClientBuilder: RestClient.Builder,
    xanoAuthClientProperties: XanoAuthClientProperties,
) {
    private val restClient = restClientBuilder
        .baseUrl(xanoAuthClientProperties.baseUrl)
        .build()

    fun login(request: UserLoginRequest): AuthResponse {
        val response = restClient.post()
            .uri("/login")
            .body(request)
            .retrieve()
            .body(object : ParameterizedTypeReference<Map<String, Any>>() {})
            ?: error("Xano login response was empty")

        return mapToAuthResponse(response)
    }

    fun register(request: UserRegistrationRequest): AuthResponse {
        val xanoRequest = mapOf(
            "full_name" to request.fullName,
            "email" to request.email,
            "phone" to request.phone,
            "password" to request.password,
        )

        val response = restClient.post()
            .uri("/register")
            .body(xanoRequest)
            .retrieve()
            .body(object : ParameterizedTypeReference<Map<String, Any>>() {})
            ?: error("Xano register response was empty")

        return mapToAuthResponse(response)
    }

    fun logout(token: String): Boolean {
        return try {
            restClient.post()
                .uri("/logout")
                .header("Authorization", "Bearer $token")
                .retrieve()
                .body(object : ParameterizedTypeReference<Map<String, Any>>() {})
            true
        } catch (e: Exception) {
            false
        }
    }

    fun me(token: String): Map<String, Any> {
        return restClient.get()
            .uri("/me")
            .header("Authorization", "Bearer $token")
            .retrieve()
            .body(object : ParameterizedTypeReference<Map<String, Any>>() {})
            ?: error("Xano me response was empty")
    }

    fun refresh(token: String): AuthResponse {
        val response = restClient.post()
            .uri("/refresh")
            .header("Authorization", "Bearer $token")
            .retrieve()
            .body(object : ParameterizedTypeReference<Map<String, Any>>() {})
            ?: error("Xano refresh response was empty")

        return mapToAuthResponse(response)
    }

    private fun mapToAuthResponse(response: Map<String, Any>): AuthResponse {
        return AuthResponse(
            userId = (response["id"] as? String) ?: (response["user_id"] as? String) ?: UUID.randomUUID().toString(),
            role = parseUserRole(response["role"] as? String),
            token = response["token"] as? String
                ?: response["auth_token"] as? String
                ?: error("No token found in Xano response"),
        )
    }

    private fun parseUserRole(roleString: String?): UserRole {
        return when (roleString?.uppercase()) {
            "ADMIN" -> UserRole.ADMIN
            "COLLABORATOR" -> UserRole.COLLABORATOR
            else -> UserRole.USER
        }
    }
}
