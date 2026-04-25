package com.sanosysalvos.collaboration.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/collaborators")
class CollaborationHealthController {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "collaboration-service",
        "status" to "up",
    )
}
