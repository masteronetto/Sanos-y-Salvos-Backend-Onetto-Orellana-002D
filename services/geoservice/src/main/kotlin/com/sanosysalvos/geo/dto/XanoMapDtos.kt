package com.sanosysalvos.geo.dto

import com.sanosysalvos.contracts.MapLayer
import com.sanosysalvos.contracts.NavigationLinkResponse
import com.sanosysalvos.contracts.NearbyReportMarker
import com.sanosysalvos.contracts.NearbyReportsResponse
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.contracts.TileProviderConfig

// DTO para la respuesta de /api:maps/provider
data class XanoProviderDto(
    val data: ProviderData? = null,
)

data class ProviderData(
    val provider: String? = "openstreetmap",
    val tileUrlTemplate: String? = "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
    val attribution: String? = "© OpenStreetMap contributors",
    val termsUrl: String? = "https://www.openstreetmap.org/copyright",
) {
    fun toTileProviderConfig(): TileProviderConfig = TileProviderConfig(
        provider = provider ?: "openstreetmap",
        tileUrlTemplate = tileUrlTemplate ?: "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
        attribution = attribution ?: "© OpenStreetMap contributors",
        termsUrl = termsUrl ?: "https://www.openstreetmap.org/copyright",
    )
}

// DTO para la respuesta de /api:maps/layers
data class XanoLayersDto(
    val data: List<LayerData>? = emptyList(),
)

data class LayerData(
    val id: String = "default",
    val displayName: String = "Default",
    val type: String = "TILE",
    val enabledByDefault: Boolean = true,
) {
    fun toMapLayer(): MapLayer = MapLayer(
        id = id,
        displayName = displayName,
        type = type,
        enabledByDefault = enabledByDefault,
    )
}

// DTO para la respuesta de /api:maps/reports/nearby
data class XanoNearbyReportsDto(
    val data: NearbyReportsData? = null,
)

data class NearbyReportsData(
    val centerLatitude: Double = 0.0,
    val centerLongitude: Double = 0.0,
    val radiusMeters: Int = 3000,
    val reports: List<ReportData>? = emptyList(),
) {
    fun toNearbyReportsResponse(): NearbyReportsResponse = NearbyReportsResponse(
        centerLatitude = centerLatitude,
        centerLongitude = centerLongitude,
        radiusMeters = radiusMeters,
        total = reports?.size ?: 0,
        markers = reports?.mapNotNull { it.toNearbyReportMarker() } ?: emptyList(),
    )
}

data class ReportData(
    val reportId: String? = null,
    val id: String? = null, // Fallback if API uses 'id' instead of 'reportId'
    val reportType: String? = "LOST",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val title: String? = "Report",
    val distanceMeters: Double? = null,
) {
    fun toNearbyReportMarker(): NearbyReportMarker? {
        val actualId = reportId ?: id ?: return null
        val type = try {
            ReportType.valueOf(reportType?.uppercase() ?: "LOST")
        } catch (_: Exception) {
            ReportType.LOST
        }
        return NearbyReportMarker(
            reportId = actualId,
            reportType = type,
            latitude = latitude,
            longitude = longitude,
            title = title ?: "Report",
            distanceMeters = distanceMeters ?: 0.0,
        )
    }
}

// DTO para la respuesta de /api:maps/navigation/link
data class XanoNavigationDto(
    val data: NavigationData? = null,
)

data class NavigationData(
    val provider: String? = "openstreetmap",
    val fromLatitude: Double = 0.0,
    val fromLongitude: Double = 0.0,
    val toLatitude: Double = 0.0,
    val toLongitude: Double = 0.0,
    val navigationUrl: String? = null,
    val googleMapsUrl: String? = null,
    val appleMapsUrl: String? = null,
    val osmUrl: String? = null,
) {
    fun toNavigationLinkResponse(): NavigationLinkResponse {
        // Preferir navigationUrl, si no está disponible, construir uno por defecto
        val url = navigationUrl
            ?: googleMapsUrl
            ?: appleMapsUrl
            ?: osmUrl
            ?: "https://www.openstreetmap.org/directions?engine=fossgis_osrm_car&route=" +
                "$fromLatitude%2C$fromLongitude%3B$toLatitude%2C$toLongitude"

        return NavigationLinkResponse(
            provider = provider ?: "openstreetmap",
            fromLatitude = fromLatitude,
            fromLongitude = fromLongitude,
            toLatitude = toLatitude,
            toLongitude = toLongitude,
            navigationUrl = url,
        )
    }
}
