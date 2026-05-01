package com.sanosysalvos.match.api

import com.sanosysalvos.contracts.ApiEnvelope
import com.sanosysalvos.contracts.MatchCandidate
import com.sanosysalvos.contracts.MatchEvaluationRequest
import com.sanosysalvos.contracts.MatchEvaluationResponse
import com.sanosysalvos.contracts.MatchNotificationRequest
import com.sanosysalvos.match.service.MatchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.DeleteMapping

@RestController
@RequestMapping("/api/v1/matches")
class MatchHealthController(
    private val matchService: MatchService,
) {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "match-service",
        "status" to "up",
    )

    @GetMapping("/pending")
    fun pending(): ApiEnvelope<List<MatchCandidate>> = ApiEnvelope(
        success = true,
        message = "Pending matches loaded from XANO",
        data = xanoMatchClient.pending(),
    )

    @PostMapping("/evaluate")
    fun evaluate(@RequestBody request: MatchEvaluationRequest): ApiEnvelope<MatchEvaluationResponse> {
        return ApiEnvelope(
            success = true,
            message = "Match evaluated",
            data = matchService.evaluate(request),
        )
    }

    @PostMapping("/notify")
    fun notifyMatch(@RequestBody request: MatchNotificationRequest): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Match notification dispatched",
        data = matchService.notifyMatch(request),
    )

    @GetMapping("/pending")
    fun pending(): ApiEnvelope<List<MatchCandidate>> = ApiEnvelope(
        success = true,
        message = "Pending matches",
        data = matchService.pending(),
    )

    @GetMapping("", "/list")
    fun listMatches(): ApiEnvelope<List<MatchCandidate>> = ApiEnvelope(
        success = true,
        message = "Matches list",
        data = matchService.listMatches(),
    )

    @GetMapping("/{id}", "/details/{id}")
    fun details(@PathVariable id: String): ApiEnvelope<MatchCandidate> = ApiEnvelope(
        success = true,
        message = "Match details",
        data = matchService.details(id),
    )

    @GetMapping("/my_matches")
    fun myMatches(@org.springframework.web.bind.annotation.RequestParam(required = false) userId: String?): ApiEnvelope<List<MatchCandidate>> = ApiEnvelope(
        success = true,
        message = "Matches for current user",
        data = matchService.myMatches(userId),
    )

    @PutMapping("/accept/{id}")
    fun accept(@PathVariable id: String): ApiEnvelope<MatchCandidate> = ApiEnvelope(
        success = true,
        message = "Match accepted",
        data = matchService.accept(id),
    )

    @PutMapping("/reject/{id}")
    fun reject(@PathVariable id: String): ApiEnvelope<MatchCandidate> = ApiEnvelope(
        success = true,
        message = "Match rejected",
        data = matchService.reject(id),
    )

    @PostMapping("/webhooks/match-notification")
    fun webhookMatchNotification(@RequestBody payload: Map<String, Any>): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Webhook received",
        data = matchService.webhookMatchNotification(payload),
    )
}
