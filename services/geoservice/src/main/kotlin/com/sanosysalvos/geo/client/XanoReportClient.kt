package com.sanosysalvos.geo.client

import com.sanosysalvos.contracts.ReportSummary
import com.sanosysalvos.contracts.ReportType
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class XanoReportClient(
    restClientBuilder: RestClient.Builder,
    @Value("\${xano.base-url}") baseUrl: String,
    @Value("\${xano.api-key:}") private val apiKey: String,
) {
    private val restClient: RestClient = restClientBuilder
        .baseUrl(baseUrl)
        .build()

    fun search(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int,
        reportType: ReportType?,
    ): List<ReportSummary> = restClient.get()
        .uri { uriBuilder ->
            val builder = uriBuilder
                .path("/sanos-y-salvos-reports/search")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("radiusMeters", radiusMeters)

            if (reportType != null) {
                builder.queryParam("reportType", reportType.name)
            }

            builder.build()
        }
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