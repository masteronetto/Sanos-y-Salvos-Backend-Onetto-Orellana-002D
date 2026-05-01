package com.sanosysalvos.user.domain

import com.sanosysalvos.contracts.UserRole
import com.sanosysalvos.common.PrefixedIdGenerator
import java.time.Instant

data class UserAccountEntity(
    var id: String? = null,
    var fullName: String = "",
    var email: String = "",
    var phone: String? = null,
    var passwordHash: String = "",
    var role: UserRole = UserRole.USER,
    var active: Boolean = true,
    var createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
) {
    fun onCreate() {
        if (id == null) {
            id = PrefixedIdGenerator.next("U")
        }
    }
}