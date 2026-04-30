package com.sanosysalvos.match.service

import com.sanosysalvos.common.NotFoundServiceException
import com.sanosysalvos.contracts.MatchCandidate
import com.sanosysalvos.contracts.MatchEvaluationRequest
import com.sanosysalvos.contracts.MatchEvaluationResponse
import com.sanosysalvos.contracts.MatchNotificationRequest
import com.sanosysalvos.match.domain.MatchEntity
import com.sanosysalvos.match.domain.MatchRepository
import com.sanosysalvos.match.domain.MatchStatus
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchRepository: MatchRepository,
) {
    fun evaluate(request: MatchEvaluationRequest): MatchEvaluationResponse {
        val candidate = MatchCandidate(
            reportId = request.lostReportId,
            matchedReportId = request.foundReportId,
            score = 0.82,
            reason = "High similarity by color, size and distance",
        )

        val entity = matchRepository.save(
            MatchEntity(
                userId = request.lostReportId,
                reportId = request.lostReportId,
                matchedReportId = request.foundReportId,
                score = candidate.score,
                reason = candidate.reason,
                status = MatchStatus.PENDING,
                notified = false,
            ),
        )

        return MatchEvaluationResponse(candidate = candidate, shouldNotify = entity.status == MatchStatus.PENDING)
    }

    fun notifyMatch(request: MatchNotificationRequest): String {
        val match = matchRepository.findAll().firstOrNull {
            it.reportId == request.match.reportId && it.matchedReportId == request.match.matchedReportId
        } ?: matchRepository.save(
            MatchEntity(
                userId = request.userId,
                reportId = request.match.reportId,
                matchedReportId = request.match.matchedReportId,
                score = request.match.score,
                reason = request.match.reason,
                status = MatchStatus.PENDING,
                notified = false,
            ),
        )

        match.notified = true
        matchRepository.save(match)
        return request.userId
    }

    fun pending(): List<MatchCandidate> = matchRepository.findAllByNotifiedFalse().map { toCandidate(it) }

    fun details(id: String): MatchCandidate = toCandidate(findMatch(id))

    fun listMatches(): List<MatchCandidate> = matchRepository.findAll().map { toCandidate(it) }

    fun myMatches(userId: String?): List<MatchCandidate> = if (userId.isNullOrBlank()) {
        listMatches()
    } else {
        matchRepository.findAllByUserId(userId).map { toCandidate(it) }
    }

    fun accept(id: String): MatchCandidate = updateStatus(id, MatchStatus.ACCEPTED)

    fun reject(id: String): MatchCandidate = updateStatus(id, MatchStatus.REJECTED)

    fun webhookMatchNotification(payload: Map<String, Any>): String {
        val matchId = payload["matchId"]?.toString() ?: return ""
        val match = matchRepository.findById(matchId).orElseThrow { NotFoundServiceException("Match not found") }
        match.notified = true
        matchRepository.save(match)
        return matchId
    }

    private fun updateStatus(id: String, status: MatchStatus): MatchCandidate {
        val match = findMatch(id)
        match.status = status
        matchRepository.save(match)
        return toCandidate(match)
    }

    private fun findMatch(id: String): MatchEntity = matchRepository.findById(id)
        .orElseThrow { NotFoundServiceException("Match not found") }

    private fun toCandidate(entity: MatchEntity): MatchCandidate = MatchCandidate(
        reportId = entity.reportId,
        matchedReportId = entity.matchedReportId,
        score = entity.score,
        reason = entity.reason,
    )
}