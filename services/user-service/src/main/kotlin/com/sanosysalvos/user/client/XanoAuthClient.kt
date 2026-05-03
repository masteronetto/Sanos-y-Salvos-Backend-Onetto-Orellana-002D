package com.sanosysalvos.user.client

import com.sanosysalvos.user.config.XanoAuthClientProperties
import com.sanosysalvos.contracts.AuthResponse
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserRegistrationRequest
import com.sanosysalvos.contracts.UserRole
import com.sanosysalvos.common.PrefixedIdGenerator
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class XanoAuthClient(
    restClientBuilder: RestClient.Builder,
    private val xanoAuthClientProperties: XanoAuthClientProperties,
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
        if (request.role == UserRole.COLLABORATOR && request.collaboratorType == null) {
            error("collaboratorType is required when role is COLLABORATOR")
        }

        val xanoRequest = mutableMapOf<String, Any?>()
        xanoRequest["fullName"] = request.fullName
        xanoRequest["email"] = request.email
        xanoRequest["phone"] = request.phone
        xanoRequest["password"] = request.password
        // Send role only if provided. Xano will default to USER if absent.
        request.role?.let { xanoRequest["role"] = it.name }
        request.collaboratorType?.let { xanoRequest["collaboratorType"] = it.name }

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

    private fun mapToAuthResponse(response: Map<String, Any>, fallbackId: String? = null): AuthResponse {
        // Xano may return { data: { uid, role, token }, message: ... } or flat { uid, role, token }
        val data = response["data"] as? Map<*, *>

        val userId = (data?.get("uid") as? String)
            ?: (data?.get("id") as? String)
            ?: (data?.get("userId") as? String)
            ?: (response["id"] as? String)
            ?: (response["uid"] as? String)
            ?: (response["userId"] as? String)
            ?: (response["user_id"] as? String)
            ?: fallbackId
            ?: PrefixedIdGenerator.next("U")

        val roleString = (data?.get("role") as? String) ?: (response["role"] as? String)

        val token = (data?.get("token") as? String)
            ?: (response["token"] as? String)
            ?: (response["auth_token"] as? String)
            ?: error("No token found in Xano response")

        return AuthResponse(
            userId = userId,
            role = parseUserRole(roleString),
            token = token,
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
