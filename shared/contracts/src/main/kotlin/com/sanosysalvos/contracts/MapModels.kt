package com.sanosysalvos.contracts

data class TileProviderConfig(
    val provider: String,
    val tileUrlTemplate: String,
    val attribution: String,
    val termsUrl: String,
)

data class MapLayer(
    val id: String,
    val displayName: String,
    val type: String,
    val enabledByDefault: Boolean,
)

data class NearbyReportMarker(
    val reportId: String,
    val reportType: ReportType,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val distanceMeters: Double,
)

data class NearbyReportsResponse(
    val centerLatitude: Double,
    val centerLongitude: Double,
    val radiusMeters: Int,
    val total: Int,
    val markers: List<NearbyReportMarker>,
)

data class NavigationLinkResponse(
    val provider: String,
    val fromLatitude: Double,
    val fromLongitude: Double,
    val toLatitude: Double,
    val toLongitude: Double,
    val navigationUrl: String,
)
