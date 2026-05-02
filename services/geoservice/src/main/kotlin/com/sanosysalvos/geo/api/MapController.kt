package com.sanosysalvos.geo.api

import com.sanosysalvos.contracts.MapLayer
import com.sanosysalvos.contracts.NavigationLinkResponse
import com.sanosysalvos.contracts.NearbyReportsResponse
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.contracts.TileProviderConfig
import com.sanosysalvos.geo.service.MapFacadeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/geo/map")
class MapController(
    private val mapFacadeService: MapFacadeService,
) {

    @GetMapping("/provider")
    fun provider(): TileProviderConfig = mapFacadeService.tileProviderConfig()

    @GetMapping("/layers")
    fun layers(): List<MapLayer> = mapFacadeService.defaultLayers()

    @GetMapping("/reports/nearby")
    fun nearbyReports(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(required = false, defaultValue = "3000") radiusMeters: Int,
        @RequestParam(required = false) reportType: ReportType?,
    ): NearbyReportsResponse = mapFacadeService.nearbyReports(
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
    ): NavigationLinkResponse = mapFacadeService.navigationLink(
        fromLatitude = fromLatitude,
        fromLongitude = fromLongitude,
        toLatitude = toLatitude,
        toLongitude = toLongitude,
    )
}
