package com.sanosysalvos.pet.client

import com.sanosysalvos.contracts.CollaboratorType
import com.sanosysalvos.contracts.ReportSummary
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class XanoCollaboratorClient(
    restClientBuilder: RestClient.Builder,
    @Value("\${xano.base-url}") baseUrl: String,
    @Value("\${xano.api-key:}") private val apiKey: String,
) {
    private val restClient: RestClient = restClientBuilder
        .baseUrl(baseUrl)
        .build()

    fun listByType(collaboratorType: CollaboratorType): List<CollaboratorRecord> = restClient.get()
        .uri("/sanos-y-salvos-collaborators/list_by_type?type={type}", collaboratorType.name)
        .applyAuth(apiKey)
        .retrieve()
        .body(object : ParameterizedTypeReference<List<CollaboratorRecord>>() {})
        ?: emptyList()

    fun recordIncident(collaboratorType: CollaboratorType, reportSummary: ReportSummary): ReportSummary = reportSummary

    data class CollaboratorRecord(
        val id: String? = null,
        val type: CollaboratorType,
        val name: String,
        val contactEmail: String,
        val contactPhone: String? = null,
        val active: Boolean = true,
    )

    private fun <T : RestClient.RequestHeadersSpec<T>> T.applyAuth(apiKey: String): T {
        if (apiKey.isNotBlank()) {
            header("Authorization", "Bearer $apiKey")
        }

        return this
    }
}