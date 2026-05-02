package com.sanosysalvos.user.api

import com.sanosysalvos.contracts.ApiEnvelope
import com.sanosysalvos.contracts.AuthResponse
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserRegistrationRequest
import com.sanosysalvos.user.client.XanoAuthClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserHealthController(
    private val xanoAuthClient: XanoAuthClient,
) {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "user-service",
        "status" to "up",
    )

    @PostMapping("/register")
    fun register(@RequestBody request: UserRegistrationRequest): ApiEnvelope<AuthResponse> = try {
        val authResponse = xanoAuthClient.register(request)
        ApiEnvelope(
            success = true,
            message = "User registered",
            data = authResponse,
        )
    } catch (e: Exception) {
        ApiEnvelope(
            success = false,
            message = "Registration failed: ${e.message}",
            data = null,
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): ApiEnvelope<AuthResponse> = try {
        val authResponse = xanoAuthClient.login(request)
        ApiEnvelope(
            success = true,
            message = "User authenticated",
            data = authResponse,
        )
    } catch (e: Exception) {
        ApiEnvelope(
            success = false,
            message = "Authentication failed: ${e.message}",
            data = null,
        )
    }

}



