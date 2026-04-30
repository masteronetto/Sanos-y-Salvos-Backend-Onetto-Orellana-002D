package com.sanosysalvos.user.api

import com.sanosysalvos.contracts.ApiEnvelope
import com.sanosysalvos.contracts.AuthResponse
import com.sanosysalvos.contracts.DeviceTokenRequest
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserRegistrationRequest
import com.sanosysalvos.contracts.UserRole
import com.sanosysalvos.user.client.XanoUserClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserHealthController(
    private val xanoUserClient: XanoUserClient,
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
        message = "User registered in XANO",
        data = xanoUserClient.register(request),
    )

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): ApiEnvelope<AuthResponse> = ApiEnvelope(
        success = true,
        message = "User authenticated in XANO",
        data = xanoUserClient.login(request),
    )

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ApiEnvelope<UserProfile> = ApiEnvelope(
        success = true,
        message = "User found",
        data = UserProfile(
            id = id,
            fullName = "Demo User",
            email = "$id",
            phone = null,
            role = UserRole.USER,
        ),
    )

    @GetMapping("/list")
    fun listUsers(): ApiEnvelope<List<UserProfile>> = ApiEnvelope(
        success = true,
        message = "User list",
        data = listOf(
            UserProfile(id = "user1", fullName = "Demo One", email = "one@example.com"),
            UserProfile(id = "user2", fullName = "Demo Two", email = "two@example.com"),
        ),
    )

    @GetMapping("/me")
    fun me(): ApiEnvelope<UserProfile> = ApiEnvelope(
        success = true,
        message = "Current user",
        data = UserProfile(id = "me", fullName = "Current User", email = "me@example.com"),
    )

    @PostMapping("/reset/request-reset-link")
    fun requestResetLink(@RequestBody body: Map<String, String>): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Reset link requested",
        data = body["email"],
    )

    @PostMapping("/reset/update_password")
    fun updatePassword(@RequestBody body: Map<String, String>): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Password updated",
        data = body["userId"],
    )

    @PostMapping("/reset/magic-link-login")
    fun magicLinkLogin(@RequestBody body: Map<String, String>): ApiEnvelope<AuthResponse> = ApiEnvelope(
        success = true,
        message = "Magic link login",
        data = AuthResponse(userId = body["userId"] ?: "", role = UserRole.USER, token = "token-magic"),
    )

    @PostMapping("/message/send_welcome_email")
    fun sendWelcomeEmail(@RequestBody body: Map<String, String>): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Welcome email queued",
        data = body["email"],
    )

    @GetMapping("/logs/my_events")
    fun myEvents(): ApiEnvelope<List<String>> = ApiEnvelope(
        success = true,
        message = "User events",
        data = listOf("login", "register"),
    )

    @PutMapping("/{userId}/role/{role}")
    fun assignRole(
        @PathVariable userId: String,
        @PathVariable role: UserRole,
    ): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Role $role assigned in XANO",
        data = xanoUserClient.assignRole(userId, role).id,
    )

    @PostMapping("/device-token")
    fun registerDeviceToken(@RequestBody request: DeviceTokenRequest): ApiEnvelope<XanoUserClient.UserRecord> = ApiEnvelope(
        success = true,
        message = "Device token stored in XANO",
        data = xanoUserClient.registerDeviceToken(request),
    )
}
