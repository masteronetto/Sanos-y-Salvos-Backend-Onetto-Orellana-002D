package com.sanosysalvos.match.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/matches")
class MatchHealthController {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "match-service",
        "status" to "up",
    )
}
