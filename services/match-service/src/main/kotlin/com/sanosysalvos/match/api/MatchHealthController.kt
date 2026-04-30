package com.sanosysalvos.match.api

import com.sanosysalvos.contracts.ApiEnvelope
import com.sanosysalvos.contracts.MatchCandidate
import com.sanosysalvos.contracts.MatchEvaluationRequest
import com.sanosysalvos.contracts.MatchEvaluationResponse
import com.sanosysalvos.contracts.MatchNotificationRequest
import com.sanosysalvos.match.client.XanoMatchClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/matches")
class MatchHealthController(
    private val xanoMatchClient: XanoMatchClient,
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
        val candidate = xanoMatchClient.evaluate(request)

        return ApiEnvelope(
            success = true,
            message = "Match evaluated in XANO",
            data = MatchEvaluationResponse(
                candidate = candidate,
                shouldNotify = true,
            ),
        )
    }

    @PostMapping("/notify")
    fun notifyMatch(@RequestBody request: MatchNotificationRequest): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Match notification dispatched through XANO",
        data = xanoMatchClient.notify(request),
    )

    @GetMapping("/pending")
    fun pending(): ApiEnvelope<List<MatchCandidate>> = ApiEnvelope(
        success = true,
        message = "Pending matches",
        data = emptyList(),
    )

    @PostMapping("/webhooks/match-notification")
    fun webhookMatchNotification(@RequestBody payload: Map<String, Any>): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Webhook received",
        data = payload["matchId"]?.toString() ?: "",
    )
}
