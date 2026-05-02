package com.sanosysalvos.bff.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "clients.xano-auth")
data class XanoAuthClientProperties(
    val baseUrl: String,
)
