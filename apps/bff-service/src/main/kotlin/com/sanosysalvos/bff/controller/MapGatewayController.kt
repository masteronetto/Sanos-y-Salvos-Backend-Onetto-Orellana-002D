package com.sanosysalvos.bff.controller

import com.sanosysalvos.bff.client.GeoServiceClient
import com.sanosysalvos.contracts.MapLayer
import com.sanosysalvos.contracts.NavigationLinkResponse
import com.sanosysalvos.contracts.NearbyReportsResponse
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.contracts.TileProviderConfig
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bff/map")
class MapGatewayController(
    private val geoServiceClient: GeoServiceClient,
) {

    @GetMapping("/provider")
    fun provider(): TileProviderConfig = geoServiceClient.provider()

    @GetMapping("/layers")
    fun layers(): List<MapLayer> = geoServiceClient.layers()

    @GetMapping("/reports/nearby")
    fun nearbyReports(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(required = false, defaultValue = "3000") radiusMeters: Int,
        @RequestParam(required = false) reportType: ReportType?,
    ): NearbyReportsResponse = geoServiceClient.nearbyReports(
        latitude = latitude,
        longitude = longitude,
        radiusMeters = radiusMeters,
        reportType = reportType,
    )

    @GetMapping("/navigation/link")
    fun navigationLink(
        @RequestParam fromLatitude: Double,
        @RequestParam fromLongitude: Double,
        @RequestParam toLatitude: Double,
        @RequestParam toLongitude: Double,
    ): NavigationLinkResponse = geoServiceClient.navigationLink(
        fromLatitude = fromLatitude,
        fromLongitude = fromLongitude,
        toLatitude = toLatitude,
        toLongitude = toLongitude,
    )
}
