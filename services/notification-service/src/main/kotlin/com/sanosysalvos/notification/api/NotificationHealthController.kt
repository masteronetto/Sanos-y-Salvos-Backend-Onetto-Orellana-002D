package com.sanosysalvos.notification.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationHealthController {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "notification-service",
        "status" to "up",
    )
}
