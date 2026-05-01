package com.sanosysalvos.geo.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.sanosysalvos.contracts.MapLayer
import com.sanosysalvos.contracts.NavigationLinkResponse
import com.sanosysalvos.contracts.NearbyReportsResponse
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.contracts.TileProviderConfig
import com.sanosysalvos.geo.dto.LayerData
import com.sanosysalvos.geo.dto.NavigationData
import com.sanosysalvos.geo.dto.ProviderData
import com.sanosysalvos.geo.dto.XanoLayersDto
import com.sanosysalvos.geo.dto.XanoNavigationDto
import com.sanosysalvos.geo.dto.XanoNearbyReportsDto
import com.sanosysalvos.geo.dto.XanoProviderDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class XanoGeoClient(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    @Value("\${xano.api.baseUrl:https://x8ki-letl-twmt.n7.xano.io}") val baseUrl: String,
) {

    fun getTileProviderConfig(): TileProviderConfig {
        val url = "$baseUrl/api:maps/provider"
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val entity = HttpEntity<Void>(headers)
        
        return try {
            val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java).body
            @Suppress("UNCHECKED_CAST")
            val typedResponse = response as? Map<String, Any>
            val dto = objectMapper.convertValue(typedResponse, XanoProviderDto::class.java)
            dto.data?.toTileProviderConfig() ?: ProviderData().toTileProviderConfig()
        } catch (e: Exception) {
            ProviderData().toTileProviderConfig()
        }
    }

    fun getMapLayers(): List<MapLayer> {
        val url = "$baseUrl/api:maps/layers"
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val entity = HttpEntity<Void>(headers)
        
        return try {
            val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java).body
            @Suppress("UNCHECKED_CAST")
            val typedResponse = response as? Map<String, Any>
            val dto = objectMapper.convertValue(typedResponse, XanoLayersDto::class.java)
            dto.data?.map { it.toMapLayer() } ?: listOf(LayerData().toMapLayer())
        } catch (e: Exception) {
            listOf(LayerData().toMapLayer())
        }
    }

    fun getNearbyReports(latitude: Double, longitude: Double, radiusMeters: Int, reportType: ReportType?): NearbyReportsResponse {
        val url = "$baseUrl/api:maps/reports/nearby?latitude=$latitude&longitude=$longitude&radiusMeters=$radiusMeters${reportType?.let { "&reportType=$it" } ?: ""}"
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val entity = HttpEntity<Void>(headers)
        
        return try {
            val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java).body
            @Suppress("UNCHECKED_CAST")
            val typedResponse = response as? Map<String, Any>
            val dto = objectMapper.convertValue(typedResponse, XanoNearbyReportsDto::class.java)
            dto.data?.toNearbyReportsResponse() ?: NearbyReportsResponse(
                centerLatitude = latitude,
                centerLongitude = longitude,
                radiusMeters = radiusMeters,
                total = 0,
                markers = emptyList(),
            )
        } catch (e: Exception) {
            NearbyReportsResponse(
                centerLatitude = latitude,
                centerLongitude = longitude,
                radiusMeters = radiusMeters,
                total = 0,
                markers = emptyList(),
            )
        }
    }

    fun getNavigationLink(fromLatitude: Double, fromLongitude: Double, toLatitude: Double, toLongitude: Double): NavigationLinkResponse {
        val url = "$baseUrl/api:maps/navigation/link?fromLatitude=$fromLatitude&fromLongitude=$fromLongitude&toLatitude=$toLatitude&toLongitude=$toLongitude"
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val entity = HttpEntity<Void>(headers)
        
        return try {
            val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java).body
            @Suppress("UNCHECKED_CAST")
            val typedResponse = response as? Map<String, Any>
            val dto = objectMapper.convertValue(typedResponse, XanoNavigationDto::class.java)
            dto.data?.toNavigationLinkResponse() ?: NavigationData(
                fromLatitude = fromLatitude,
                fromLongitude = fromLongitude,
                toLatitude = toLatitude,
                toLongitude = toLongitude,
            ).toNavigationLinkResponse()
        } catch (e: Exception) {
            NavigationData(
                fromLatitude = fromLatitude,
                fromLongitude = fromLongitude,
                toLatitude = toLatitude,
                toLongitude = toLongitude,
            ).toNavigationLinkResponse()
        }
    }
}
