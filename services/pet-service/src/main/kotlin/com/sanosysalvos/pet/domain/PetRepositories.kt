package com.sanosysalvos.pet.domain

import com.sanosysalvos.contracts.CollaboratorType
import com.sanosysalvos.contracts.ReportType
import org.springframework.data.jpa.repository.JpaRepository

interface PetRepository : JpaRepository<PetEntity, String> {
    fun findAllByOwnerId(ownerId: String): List<PetEntity>
}

interface ReportRepository : JpaRepository<ReportEntity, String> {
    fun findAllByPetId(petId: String): List<ReportEntity>
    fun findAllByReporterId(reporterId: String): List<ReportEntity>
    fun findAllByType(type: ReportType): List<ReportEntity>
}

interface CollaboratorRepository : JpaRepository<CollaboratorEntity, String> {
    fun findAllByType(type: CollaboratorType): List<CollaboratorEntity>
}