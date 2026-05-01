package com.sanosysalvos.match.client

import com.sanosysalvos.contracts.MatchCandidate
import com.sanosysalvos.contracts.MatchEvaluationRequest
import com.sanosysalvos.contracts.MatchEvaluationResponse
import com.sanosysalvos.contracts.MatchNotificationRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject

@Component
class XanoMatchClient(
    private val restTemplate: RestTemplate,
    @Value("\${xano.api.baseUrl:https://x8ki-letl-twmt.n7.xano.io}") val baseUrl: String,
) {

    fun evaluate(request: MatchEvaluationRequest, token: String): MatchEvaluationResponse {
        val url = "$baseUrl/api:matches/evaluate"
        val headers = authHeaders(token)
        val entity = HttpEntity(request, headers)
        val response = restTemplate.postForObject<Map<String, Any>>(url, entity)
        return parseMatchEvaluationResponse(response)
    }

    fun pending(token: String): List<MatchCandidate> {
        val url = "$baseUrl/api:matches/pending"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parseMatchCandidate(it) } ?: emptyList()
    }

    fun listMatches(token: String): List<MatchCandidate> {
        val url = "$baseUrl/api:matches/list"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parseMatchCandidate(it) } ?: emptyList()
    }

    fun getMatchDetails(id: String, token: String): MatchCandidate {
        val url = "$baseUrl/api:matches/details/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parseMatchCandidate(typedResponse)
    }

    fun myMatches(token: String): List<MatchCandidate> {
        val url = "$baseUrl/api:matches/my_matches"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parseMatchCandidate(it) } ?: emptyList()
    }

    fun acceptMatch(id: String, token: String): MatchCandidate {
        val url = "$baseUrl/api:matches/accept/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.POST, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parseMatchCandidate(typedResponse)
    }

    fun rejectMatch(id: String, token: String): MatchCandidate {
        val url = "$baseUrl/api:matches/reject/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.POST, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parseMatchCandidate(typedResponse)
    }

    fun webhookNotification(request: MatchNotificationRequest): MatchNotificationRequest {
        val url = "$baseUrl/api:matches/match-notification"
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val entity = HttpEntity(request, headers)
        val response = restTemplate.postForObject<Map<String, Any>>(url, entity)
        return parseMatchNotificationRequest(response)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseMatchEvaluationResponse(response: Map<String, Any>?): MatchEvaluationResponse {
        val data = (response?.get("data") as? Map<String, Any>) ?: response ?: emptyMap()
        val candidateData = (data["candidate"] as? Map<String, Any>) ?: data
        return MatchEvaluationResponse(
            candidate = parseMatchCandidate(candidateData),
            shouldNotify = (data["shouldNotify"] as? Boolean) ?: false,
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseMatchCandidate(response: Map<String, Any>?): MatchCandidate {
        val data = (response?.get("data") as? Map<String, Any>) ?: response ?: emptyMap()
        return MatchCandidate(
            reportId = data["reportId"]?.toString() ?: "",
            matchedReportId = data["matchedReportId"]?.toString() ?: "",
            score = (data["score"] as? Number)?.toDouble() ?: 0.0,
            reason = data["reason"]?.toString() ?: "",
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseMatchNotificationRequest(response: Map<String, Any>?): MatchNotificationRequest {
        val data = (response?.get("data") as? Map<String, Any>) ?: response ?: emptyMap()
        val matchData = (data["match"] as? Map<String, Any>) ?: data
        return MatchNotificationRequest(
            userId = data["userId"]?.toString() ?: "",
            match = parseMatchCandidate(matchData),
        )
    }

    private fun authHeaders(token: String): HttpHeaders {
        return HttpHeaders().apply {
            add("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
        }
    }
}
