package com.sanosysalvos.contracts

enum class ReportType {
    LOST,
    FOUND,
}

data class ReportSummary(
    val id: String? = null,
    val type: ReportType,
    val petId: String? = null,
    val reporterId: String,
    val description: String,
    val location: GeoPoint,
    val eventDate: String,
    val photoUrl: String? = null,
)

data class MatchCandidate(
    val reportId: String,
    val matchedReportId: String,
    val score: Double,
    val reason: String,
)
