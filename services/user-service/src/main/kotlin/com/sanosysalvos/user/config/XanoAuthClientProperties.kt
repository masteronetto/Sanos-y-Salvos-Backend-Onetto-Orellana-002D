package com.sanosysalvos.user.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "clients.xano-auth")
data class XanoAuthClientProperties(
    val baseUrl: String = "https://x8ki-letl-twmt.n7.xano.io/api:sanos-y-salvos-auth",
)
