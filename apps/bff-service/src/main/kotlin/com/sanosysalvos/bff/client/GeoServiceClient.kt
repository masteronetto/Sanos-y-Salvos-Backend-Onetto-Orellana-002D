package com.sanosysalvos.bff.client

import com.sanosysalvos.bff.config.GeoServiceClientProperties
import com.sanosysalvos.contracts.MapLayer
import com.sanosysalvos.contracts.NavigationLinkResponse
import com.sanosysalvos.contracts.NearbyReportsResponse
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.contracts.TileProviderConfig
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class GeoServiceClient(
    restClientBuilder: RestClient.Builder,
    geoServiceClientProperties: GeoServiceClientProperties,
) {
    private val restClient = restClientBuilder
        .baseUrl(geoServiceClientProperties.baseUrl)
        .build()

    fun provider(): TileProviderConfig = restClient.get()
        .uri("/api/v1/geo/map/provider")
        .retrieve()
        .body(TileProviderConfig::class.java)
        ?: error("GeoService provider response was empty")

    fun layers(): List<MapLayer> = restClient.get()
        .uri("/api/v1/geo/map/layers")
        .retrieve()
        .body(object : ParameterizedTypeReference<List<MapLayer>>() {})
        ?: emptyList()

    fun nearbyReports(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int,
        reportType: ReportType?,
    ): NearbyReportsResponse {
        val reportTypeQuery = reportType?.name

        return restClient.get()
            .uri { uriBuilder ->
                val builder = uriBuilder
                    .path("/api/v1/geo/map/reports/nearby")
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .queryParam("radiusMeters", radiusMeters)

                if (reportTypeQuery != null) {
                    builder.queryParam("reportType", reportTypeQuery)
                }

                builder.build()
            }
            .retrieve()
            .body(NearbyReportsResponse::class.java)
            ?: error("GeoService nearby reports response was empty")
    }

    fun navigationLink(
        fromLatitude: Double,
        fromLongitude: Double,
        toLatitude: Double,
        toLongitude: Double,
    ): NavigationLinkResponse = restClient.get()
        .uri { uriBuilder ->
            uriBuilder
                .path("/api/v1/geo/map/navigation/link")
                .queryParam("fromLatitude", fromLatitude)
                .queryParam("fromLongitude", fromLongitude)
                .queryParam("toLatitude", toLatitude)
                .queryParam("toLongitude", toLongitude)
                .build()
        }
        .retrieve()
        .body(NavigationLinkResponse::class.java)
        ?: error("GeoService navigation link response was empty")
}
