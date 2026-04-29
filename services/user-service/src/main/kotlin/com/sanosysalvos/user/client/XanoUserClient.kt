package com.sanosysalvos.user.client

import com.sanosysalvos.contracts.AuthResponse
import com.sanosysalvos.contracts.DeviceTokenRequest
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserRegistrationRequest
import com.sanosysalvos.contracts.UserRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class XanoUserClient(
    restClientBuilder: RestClient.Builder,
    @Value("\${xano.base-url}") baseUrl: String,
    @Value("\${xano.api-key:}") private val apiKey: String,
) {
    private val restClient: RestClient = restClientBuilder
        .baseUrl(baseUrl)
        .build()

    fun register(request: UserRegistrationRequest): AuthResponse = restClient.post()
        .uri("/sanos-y-salvos-auth/register")
        .applyAuth(apiKey)
        .body(request)
        .retrieve()
        .body(AuthResponse::class.java)
        ?: error("XANO register response was empty")

    fun login(request: UserLoginRequest): AuthResponse = restClient.post()
        .uri("/sanos-y-salvos-auth/login")
        .applyAuth(apiKey)
        .body(request)
        .retrieve()
        .body(AuthResponse::class.java)
        ?: error("XANO login response was empty")

    fun listUsers(): List<UserRecord> = restClient.get()
        .uri("/sanos-y-salvos-users/list")
        .applyAuth(apiKey)
        .retrieve()
        .body(object : ParameterizedTypeReference<List<UserRecord>>() {})
        ?: emptyList()

    fun getById(userId: String): UserRecord = restClient.get()
        .uri("/sanos-y-salvos-users/get_by_id/{id}", userId)
        .applyAuth(apiKey)
        .retrieve()
        .body(UserRecord::class.java)
        ?: error("XANO getById response was empty")

    fun assignRole(userId: String, role: UserRole): UserRecord = restClient.put()
        .uri("/sanos-y-salvos-users/update/{id}", userId)
        .applyAuth(apiKey)
        .body(mapOf("role" to role.name))
        .retrieve()
        .body(UserRecord::class.java)
        ?: error("XANO assignRole response was empty")

    fun registerDeviceToken(request: DeviceTokenRequest): UserRecord = restClient.put()
        .uri("/sanos-y-salvos-users/update/{id}", request.userId)
        .applyAuth(apiKey)
        .body(mapOf("deviceToken" to request.deviceToken))
        .retrieve()
        .body(UserRecord::class.java)
        ?: error("XANO device token response was empty")

    data class UserRecord(
        val id: String,
        val fullName: String,
        val email: String,
        val phone: String? = null,
        val role: UserRole = UserRole.USER,
        val deviceToken: String? = null,
    )

    private fun <T : RestClient.RequestHeadersSpec<T>> T.applyAuth(apiKey: String): T {
        if (apiKey.isNotBlank()) {
            header("Authorization", "Bearer $apiKey")
        }

        return this
    }
}