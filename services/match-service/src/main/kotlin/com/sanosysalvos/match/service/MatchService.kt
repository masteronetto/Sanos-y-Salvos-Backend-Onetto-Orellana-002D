package com.sanosysalvos.match.service

import com.sanosysalvos.contracts.MatchCandidate
import com.sanosysalvos.contracts.MatchEvaluationRequest
import com.sanosysalvos.contracts.MatchEvaluationResponse
import com.sanosysalvos.contracts.MatchNotificationRequest
import com.sanosysalvos.match.client.XanoMatchClient
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val xanoMatchClient: XanoMatchClient,
) {

    fun evaluate(request: MatchEvaluationRequest): MatchEvaluationResponse =
        xanoMatchClient.evaluate(request, "")

    fun notifyMatch(request: MatchNotificationRequest): String {
        xanoMatchClient.webhookNotification(request)
        return request.userId
    }

    fun pending(): List<MatchCandidate> =
        xanoMatchClient.pending("")

    fun details(id: String): MatchCandidate =
        xanoMatchClient.getMatchDetails(id, "")

    fun listMatches(): List<MatchCandidate> =
        xanoMatchClient.listMatches("")

    fun myMatches(userId: String?): List<MatchCandidate> =
        xanoMatchClient.myMatches("")

    fun accept(id: String): MatchCandidate =
        xanoMatchClient.acceptMatch(id, "")

    fun reject(id: String): MatchCandidate =
        xanoMatchClient.rejectMatch(id, "")

    fun webhookMatchNotification(payload: Map<String, Any>): String {
        // Xano handles webhooks server-side
        return payload["matchId"]?.toString() ?: ""
    }
}
