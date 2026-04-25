package com.sanosysalvos.report.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reports")
class ReportHealthController {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "report-service",
        "status" to "up",
    )
}
