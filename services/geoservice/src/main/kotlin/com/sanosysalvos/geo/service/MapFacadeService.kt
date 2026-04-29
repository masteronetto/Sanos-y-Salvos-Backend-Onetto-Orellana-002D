package com.sanosysalvos.geo.service

import com.sanosysalvos.contracts.MapLayer
import com.sanosysalvos.contracts.NavigationLinkResponse
import com.sanosysalvos.contracts.NearbyReportMarker
import com.sanosysalvos.contracts.NearbyReportsResponse
import com.sanosysalvos.contracts.ReportSummary
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.contracts.TileProviderConfig
import com.sanosysalvos.geo.client.XanoReportClient
import com.sanosysalvos.geo.config.OsmProperties
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import org.springframework.stereotype.Service

@Service
class MapFacadeService(
    private val osmProperties: OsmProperties,
    private val xanoReportClient: XanoReportClient,
) {
    fun tileProviderConfig(): TileProviderConfig = TileProviderConfig(
        provider = "openstreetmap",
        tileUrlTemplate = osmProperties.tileUrlTemplate,
        attribution = osmProperties.attribution,
        termsUrl = osmProperties.termsUrl,
    )

    fun defaultLayers(): List<MapLayer> = listOf(
        MapLayer("streets", "Calles", "BASE", true),
        MapLayer("paths", "Caminos", "OVERLAY", true),
        MapLayer("report-markers", "Reportes cercanos", "OVERLAY", true),
        MapLayer("navigation-preview", "Navegacion", "OVERLAY", true),
    )

    fun nearbyReports(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int,
        reportType: ReportType?,
    ): NearbyReportsResponse {
        val reports = runCatching {
            xanoReportClient.search(latitude, longitude, radiusMeters, reportType)
        }.getOrElse {
            fallbackReports()
        }

        val markers = reports
            .mapNotNull { report ->
                val reportLatitude = report.location.latitude
                val reportLongitude = report.location.longitude
                val distance = haversineMeters(latitude, longitude, reportLatitude, reportLongitude)
                val reportId = report.id ?: return@mapNotNull null

                NearbyReportMarker(
                    reportId = reportId,
                    reportType = report.type,
                    latitude = reportLatitude,
                    longitude = reportLongitude,
                    title = report.description,
                    distanceMeters = distance,
                )
            }
            .filter { marker -> marker.distanceMeters <= radiusMeters }
            .filter { marker -> reportType == null || marker.reportType == reportType }
            .sortedBy { marker -> marker.distanceMeters }

        return NearbyReportsResponse(
            centerLatitude = latitude,
            centerLongitude = longitude,
            radiusMeters = radiusMeters,
            total = markers.size,
            markers = markers,
        )
    }

    fun navigationLink(
        fromLatitude: Double,
        fromLongitude: Double,
        toLatitude: Double,
        toLongitude: Double,
    ): NavigationLinkResponse {
        val url = "https://www.openstreetmap.org/directions?engine=fossgis_osrm_car&route=" +
            "$fromLatitude%2C$fromLongitude%3B$toLatitude%2C$toLongitude"

        return NavigationLinkResponse(
            provider = "openstreetmap",
            fromLatitude = fromLatitude,
            fromLongitude = fromLongitude,
            toLatitude = toLatitude,
            toLongitude = toLongitude,
            navigationUrl = url,
        )
    }

    private fun haversineMeters(
        latitudeA: Double,
        longitudeA: Double,
        latitudeB: Double,
        longitudeB: Double,
    ): Double {
        val earthRadiusMeters = 6_371_000.0
        val lat1 = Math.toRadians(latitudeA)
        val lon1 = Math.toRadians(longitudeA)
        val lat2 = Math.toRadians(latitudeB)
        val lon2 = Math.toRadians(longitudeB)

        val latitudeDiff = lat2 - lat1
        val longitudeDiff = lon2 - lon1

        val haversine = sin(latitudeDiff / 2).pow(2) +
            cos(lat1) * cos(lat2) * sin(longitudeDiff / 2).pow(2)

        return 2 * earthRadiusMeters * asin(sqrt(haversine))
    }

    private fun fallbackReports(): List<ReportSummary> = listOf(
        ReportSummary(
            id = "RPT-001",
            type = ReportType.LOST,
            petId = null,
            reporterId = "fallback-user",
            description = "Perro perdido cerca del parque",
            location = com.sanosysalvos.contracts.GeoPoint(-33.4489, -70.6693),
            eventDate = "2026-01-01T12:00:00Z",
            photoUrl = null,
        ),
        ReportSummary(
            id = "RPT-002",
            type = ReportType.FOUND,
            petId = null,
            reporterId = "fallback-user",
            description = "Gato encontrado en avenida central",
            location = com.sanosysalvos.contracts.GeoPoint(-33.4512, -70.6619),
            eventDate = "2026-01-02T12:00:00Z",
            photoUrl = null,
        ),
    )
}
