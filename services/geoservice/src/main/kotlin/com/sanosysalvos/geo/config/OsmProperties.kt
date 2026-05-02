package com.sanosysalvos.geo.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "geo.osm")
data class OsmProperties(
    val tileUrlTemplate: String,
    val attribution: String,
    val termsUrl: String,
)
