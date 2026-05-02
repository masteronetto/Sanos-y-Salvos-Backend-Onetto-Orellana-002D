package com.sanosysalvos.user.client

import com.sanosysalvos.contracts.AuthResponse
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserProfile
import com.sanosysalvos.contracts.UserRegistrationRequest
import com.sanosysalvos.contracts.UserRole
import com.sanosysalvos.common.PrefixedIdGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject

@Suppress("UNCHECKED_CAST")
@Component
class XanoUserClient(
    private val restTemplate: RestTemplate,
    @Value("\${xano.api.baseUrl:https://x8ki-letl-twmt.n7.xano.io}") val baseUrl: String,
) {
    
    fun register(request: UserRegistrationRequest): AuthResponse {
        val url = "$baseUrl/api:sanos-y-salvos-auth/register"
        val generatedId = generateUserId()
        val payload = mapOf(
            "id" to generatedId,
            "fullName" to request.fullName,
            "email" to request.email,
            "phone" to request.phone,
            "password" to request.password,
        )
        val response = restTemplate.postForObject<Map<String, Any>>(url, payload)
        return parseAuthResponse(response)
    }

    fun login(request: UserLoginRequest): AuthResponse {
        val url = "$baseUrl/api:sanos-y-salvos-auth/login"
        val response = restTemplate.postForObject<Map<String, Any>>(url, request)
        return parseAuthResponse(response)
    }

    fun getMe(token: String): UserProfile {
        val url = "$baseUrl/api:sanos-y-salvos-auth/me"
        val headers = HttpHeaders().apply {
            add("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parseUserProfile(typedResponse)
    }

    fun getById(id: String, token: String): UserProfile {
        val url = "$baseUrl/api:sanos-y-salvos-auth/users/$id"
        val headers = HttpHeaders().apply {
            add("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parseUserProfile(typedResponse)
    }

    fun listUsers(token: String): List<UserProfile> {
        val url = "$baseUrl/api:sanos-y-salvos-auth/users/list"
        val headers = HttpHeaders().apply {
            add("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parseUserProfile(it) } ?: emptyList()
    }

    fun updateUser(id: String, profile: UserProfile, token: String): UserProfile {
        val url = "$baseUrl/api:sanos-y-salvos-auth/users/$id"
        val headers = HttpHeaders().apply {
            add("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity(profile, headers)
        val response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parseUserProfile(typedResponse)
    }

    fun deleteUser(id: String, token: String): String {
        val url = "$baseUrl/api:sanos-y-salvos-auth/users/$id"
        val headers = HttpHeaders().apply {
            add("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
        }
        val entity = HttpEntity<Void>(headers)
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void::class.java)
        return id
    }

    fun refresh(refreshToken: String): AuthResponse {
        val url = "$baseUrl/api:sanos-y-salvos-auth/refresh"
        val body = mapOf("refreshToken" to refreshToken)
        val response = restTemplate.postForObject<Map<String, Any>>(url, body)
        return parseAuthResponse(response)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseAuthResponse(response: Map<String, Any>?): AuthResponse {
        val data = (response?.get("data") as? Map<String, Any>) ?: response ?: emptyMap()
        return AuthResponse(
            userId = data["userId"]?.toString() ?: data["id"]?.toString() ?: "",
            role = UserRole.valueOf((data["role"]?.toString() ?: "USER").uppercase()),
            token = data["token"]?.toString() ?: "",
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun generateUserId(): String {
        return try {
            val url = "$baseUrl/api:sanos-y-salvos-users/list"
            val response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity<Void>(HttpHeaders()), List::class.java).body
            val ids = (response as? List<Map<String, Any>>)
                ?.map { it["id"]?.toString() }
                ?: emptyList()
            PrefixedIdGenerator.next("U", ids)
        } catch (_: Exception) {
            PrefixedIdGenerator.next("U")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseUserProfile(response: Map<String, Any>?): UserProfile {
        val data = (response?.get("data") as? Map<String, Any>) ?: response ?: emptyMap()
        return UserProfile(
            id = data["id"]?.toString() ?: "",
            fullName = data["fullName"]?.toString() ?: "",
            email = data["email"]?.toString() ?: "",
            phone = data["phone"]?.toString(),
            role = UserRole.valueOf((data["role"]?.toString() ?: "USER").uppercase()),
        )
    }
}
