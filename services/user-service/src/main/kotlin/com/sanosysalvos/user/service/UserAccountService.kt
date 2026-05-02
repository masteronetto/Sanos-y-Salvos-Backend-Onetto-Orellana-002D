package com.sanosysalvos.user.service

import com.sanosysalvos.contracts.AuthResponse
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserRegistrationRequest
import com.sanosysalvos.user.client.XanoUserClient
import org.springframework.stereotype.Service

@Service
class UserAccountService(
    private val xanoUserClient: XanoUserClient,
) {
    fun register(request: UserRegistrationRequest): AuthResponse =
        xanoUserClient.register(request)
    
    fun login(request: UserLoginRequest): AuthResponse =
        xanoUserClient.login(request)
}