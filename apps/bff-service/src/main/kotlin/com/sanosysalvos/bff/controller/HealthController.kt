package com.sanosysalvos.bff.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bff")
class HealthController {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "bff-service",
        "status" to "up",
    )
}
