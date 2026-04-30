package com.sanosysalvos.user.api

import com.sanosysalvos.contracts.ApiEnvelope
import com.sanosysalvos.contracts.AuthResponse
import com.sanosysalvos.contracts.DeviceTokenRequest
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserProfile
import com.sanosysalvos.contracts.UserRegistrationRequest
import com.sanosysalvos.contracts.UserRole
import com.sanosysalvos.contracts.UserUpdateRequest
import com.sanosysalvos.user.service.UserAccountService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
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
    private val userAccountService: UserAccountService,
) {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "user-service",
        "status" to "up",
    )

    @PostMapping("/register")
    fun register(@RequestBody request: UserRegistrationRequest): ApiEnvelope<AuthResponse> = ApiEnvelope(
        success = true,
        message = "User registered",
        data = userAccountService.register(request),
    )

    @PostMapping("/create")
    fun create(@RequestBody request: UserRegistrationRequest): ApiEnvelope<UserProfile> = ApiEnvelope(
        success = true,
        message = "User created",
        data = userAccountService.create(request),
    )

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): ApiEnvelope<AuthResponse> = ApiEnvelope(
        success = true,
        message = "User authenticated",
        data = userAccountService.login(request),
    )

    @PostMapping("/logout")
    fun logout(authentication: Authentication): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "User logged out",
        data = userAccountService.logout(authentication),
    )

    @PostMapping("/refresh")
    fun refresh(@RequestBody body: Map<String, String>): ApiEnvelope<AuthResponse> = ApiEnvelope(
        success = true,
        message = "Token refreshed",
        data = userAccountService.refresh(body["refreshToken"] ?: body["token"] ?: error("refreshToken is required")),
    )

    @GetMapping("/{id}", "/get_by_id/{id}")
    fun getById(@PathVariable id: String): ApiEnvelope<UserProfile> = ApiEnvelope(
        success = true,
        message = "User found",
        data = userAccountService.getById(id),
    )

    @GetMapping("/list")
    fun listUsers(): ApiEnvelope<List<UserProfile>> = ApiEnvelope(
        success = true,
        message = "User list",
        data = userAccountService.listUsers(),
    )

    @GetMapping("/me")
    fun me(authentication: Authentication): ApiEnvelope<UserProfile> = ApiEnvelope(
        success = true,
        message = "Current user",
        data = userAccountService.me(authentication),
    )

    @PostMapping("/reset/request-reset-link")
    fun requestResetLink(@RequestBody body: Map<String, String>): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Reset link requested",
        data = userAccountService.requestResetLink(body["email"] ?: error("email is required")),
    )

    @PostMapping("/reset/update_password")
    fun updatePassword(@RequestBody body: Map<String, String>): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Password updated",
        data = userAccountService.updatePassword(
            userId = body["userId"] ?: error("userId is required"),
            token = body["token"] ?: error("token is required"),
            newPassword = body["newPassword"] ?: error("newPassword is required"),
        ),
    )

    @PostMapping("/reset/magic-link-login")
    fun magicLinkLogin(@RequestBody body: Map<String, String>): ApiEnvelope<AuthResponse> = ApiEnvelope(
        success = true,
        message = "Magic link login",
        data = userAccountService.magicLinkLogin(body["token"] ?: error("token is required")),
    )

    @PostMapping("/message/send_welcome_email")
    fun sendWelcomeEmail(@RequestBody body: Map<String, String>): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Welcome email queued",
        data = userAccountService.sendWelcomeEmail(
            userId = body["userId"] ?: body["email"] ?: error("userId or email is required"),
            email = body["email"] ?: error("email is required"),
        ),
    )

    @GetMapping("/logs/my_events")
    fun myEvents(authentication: Authentication): ApiEnvelope<List<String>> = ApiEnvelope(
        success = true,
        message = "User events",
        data = userAccountService.myEvents(authentication),
    )

    @PutMapping("/{userId}/role/{role}")
    fun assignRole(
        @PathVariable userId: String,
        @PathVariable role: UserRole,
    ): ApiEnvelope<UserProfile> = ApiEnvelope(
        success = true,
        message = "Role updated",
        data = userAccountService.assignRole(userId, role),
    )

    @PutMapping("/update/{userId}")
    fun update(
        @PathVariable userId: String,
        @RequestBody request: UserUpdateRequest,
    ): ApiEnvelope<UserProfile> = ApiEnvelope(
        success = true,
        message = "User updated",
        data = userAccountService.update(userId, request),
    )

    @DeleteMapping("/{id}", "/delete/{id}")
    fun delete(@PathVariable id: String): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "User deleted",
        data = userAccountService.delete(id),
    )

    @PostMapping("/device-token")
    fun registerDeviceToken(@RequestBody request: DeviceTokenRequest): ApiEnvelope<DeviceTokenRequest> = ApiEnvelope(
        success = true,
        message = "Device token stored",
        data = request.copy(
            deviceToken = userAccountService.registerDeviceToken(request.userId, request.deviceToken),
        ),
    )
}
