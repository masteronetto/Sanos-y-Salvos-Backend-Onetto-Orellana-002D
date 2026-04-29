package com.sanosysalvos.pet.client

import com.sanosysalvos.contracts.PetProfile
import com.sanosysalvos.contracts.ReportSummary
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class XanoPetClient(
    restClientBuilder: RestClient.Builder,
    @Value("\${xano.base-url}") baseUrl: String,
    @Value("\${xano.api-key:}") private val apiKey: String,
) {
    private val restClient: RestClient = restClientBuilder
        .baseUrl(baseUrl)
        .build()

    fun create(petProfile: PetProfile): PetProfile = restClient.post()
        .uri("/sanos-y-salvos-pets/create")
        .applyAuth(apiKey)
        .body(petProfile)
        .retrieve()
        .body(PetProfile::class.java)
        ?: error("XANO pet create response was empty")

    fun listByOwner(ownerId: String): List<PetProfile> = restClient.get()
        .uri("/sanos-y-salvos-pets/list_by_owner/{ownerId}", ownerId)
        .applyAuth(apiKey)
        .retrieve()
        .body(object : ParameterizedTypeReference<List<PetProfile>>() {})
        ?: emptyList()

    fun createReport(petId: String, reportSummary: ReportSummary): ReportSummary = restClient.post()
        .uri("/sanos-y-salvos-reports/create")
        .applyAuth(apiKey)
        .body(reportSummary.copy(petId = petId))
        .retrieve()
        .body(ReportSummary::class.java)
        ?: error("XANO report create response was empty")

    fun reportHistory(petId: String): List<ReportSummary> = restClient.get()
        .uri("/sanos-y-salvos-reports/list_by_pet/{petId}", petId)
        .applyAuth(apiKey)
        .retrieve()
        .body(object : ParameterizedTypeReference<List<ReportSummary>>() {})
        ?: emptyList()

    private fun <T : RestClient.RequestHeadersSpec<T>> T.applyAuth(apiKey: String): T {
        if (apiKey.isNotBlank()) {
            header("Authorization", "Bearer $apiKey")
        }

        return this
    }
}