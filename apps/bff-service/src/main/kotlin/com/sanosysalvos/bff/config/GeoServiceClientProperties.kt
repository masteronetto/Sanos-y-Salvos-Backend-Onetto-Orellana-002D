package com.sanosysalvos.bff.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "clients.geoservice")
data class GeoServiceClientProperties(
    val baseUrl: String,
)
