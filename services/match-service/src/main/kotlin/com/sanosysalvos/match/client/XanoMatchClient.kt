package com.sanosysalvos.match.client

import com.sanosysalvos.contracts.MatchCandidate
import com.sanosysalvos.contracts.MatchEvaluationRequest
import com.sanosysalvos.contracts.MatchNotificationRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class XanoMatchClient(
    restClientBuilder: RestClient.Builder,
    @Value("\${xano.base-url}") baseUrl: String,
    @Value("\${xano.api-key:}") private val apiKey: String,
) {
    private val restClient: RestClient = restClientBuilder
        .baseUrl(baseUrl)
        .build()

    fun pending(): List<MatchCandidate> = restClient.get()
        .uri("/sanos-y-salvos-matches/pending")
        .applyAuth(apiKey)
        .retrieve()
        .body(object : ParameterizedTypeReference<List<MatchCandidate>>() {})
        ?: emptyList()

    fun evaluate(request: MatchEvaluationRequest): MatchCandidate = restClient.post()
        .uri("/sanos-y-salvos-matches/create")
        .applyAuth(apiKey)
        .body(
            mapOf(
                "lostReportId" to request.lostReportId,
                "foundReportId" to request.foundReportId,
            ),
        )
        .retrieve()
        .body(MatchCandidate::class.java)
        ?: MatchCandidate(
            reportId = request.lostReportId,
            matchedReportId = request.foundReportId,
            score = 0.0,
            reason = "XANO match response was empty",
        )

    fun notify(request: MatchNotificationRequest): String = restClient.post()
        .uri("/sanos-y-salvos-webhooks/match-notification")
        .applyAuth(apiKey)
        .body(
            mapOf(
                "userId" to request.userId,
                "matchId" to "${request.match.reportId}:${request.match.matchedReportId}",
                "score" to request.match.score,
            ),
        )
        .retrieve()
        .body(String::class.java)
        ?: request.userId

    private fun <T : RestClient.RequestHeadersSpec<T>> T.applyAuth(apiKey: String): T {
        if (apiKey.isNotBlank()) {
            header("Authorization", "Bearer $apiKey")
        }

        return this
    }
}