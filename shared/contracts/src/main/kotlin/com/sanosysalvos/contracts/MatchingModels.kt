package com.sanosysalvos.contracts

data class MatchEvaluationRequest(
    val lostReportId: String,
    val foundReportId: String,
)

data class MatchEvaluationResponse(
    val candidate: MatchCandidate,
    val shouldNotify: Boolean,
)

data class MatchNotificationRequest(
    val userId: String,
    val match: MatchCandidate,
)
