package com.sanosysalvos.user.api

import com.sanosysalvos.contracts.ApiEnvelope
import com.sanosysalvos.contracts.AuthResponse
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserRegistrationRequest
import com.sanosysalvos.user.service.UserAccountService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserHealthController(
    private val userAccountService: UserAccountService,
) {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "user-service",
        "status" to "up",
    )

    @GetMapping("/list")
    fun listUsers(): ApiEnvelope<List<XanoUserClient.UserRecord>> = ApiEnvelope(
        success = true,
        message = "User list loaded from XANO",
        data = xanoUserClient.listUsers(),
    )

    @GetMapping("/{userId}")
    fun getById(
        @PathVariable userId: String,
    ): ApiEnvelope<XanoUserClient.UserRecord> = ApiEnvelope(
        success = true,
        message = "User loaded from XANO",
        data = xanoUserClient.getById(userId),
    )

    @PostMapping("/register")
    fun register(@RequestBody request: UserRegistrationRequest): ApiEnvelope<AuthResponse> = ApiEnvelope(
        success = true,
        message = "User registered",
        data = userAccountService.register(request),
    )


    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): ApiEnvelope<AuthResponse> = ApiEnvelope(
        success = true,
        message = "User authenticated",
        data = userAccountService.login(request),
    )

}
