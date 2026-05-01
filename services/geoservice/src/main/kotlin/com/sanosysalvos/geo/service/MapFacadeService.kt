package com.sanosysalvos.geo.service

import com.sanosysalvos.contracts.MapLayer
import com.sanosysalvos.contracts.NavigationLinkResponse
import com.sanosysalvos.contracts.NearbyReportsResponse
import com.sanosysalvos.contracts.ReportSummary
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.contracts.TileProviderConfig
import com.sanosysalvos.geo.client.XanoGeoClient
import org.springframework.stereotype.Service

@Service
class MapFacadeService(
    private val xanoGeoClient: XanoGeoClient,
) {

    fun tileProviderConfig(): TileProviderConfig = xanoGeoClient.getTileProviderConfig()

    fun defaultLayers(): List<MapLayer> = xanoGeoClient.getMapLayers()

    fun nearbyReports(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int,
        reportType: ReportType?,
    ): NearbyReportsResponse = xanoGeoClient.getNearbyReports(latitude, longitude, radiusMeters, reportType)

    fun navigationLink(
        fromLatitude: Double,
        fromLongitude: Double,
        toLatitude: Double,
        toLongitude: Double,
    ): NavigationLinkResponse = xanoGeoClient.getNavigationLink(fromLatitude, fromLongitude, toLatitude, toLongitude)
}
