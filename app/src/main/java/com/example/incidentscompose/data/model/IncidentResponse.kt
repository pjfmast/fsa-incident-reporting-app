package com.example.incidentscompose.data.model

import kotlinx.serialization.Serializable

@Serializable
data class IncidentResponse(
    val id: Long,
    val reportedBy: Long?,
    val category: IncidentCategory,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val images: List<String>,
    val priority: Priority,
    val status: Status,
    val createdAt: String,
    val updatedAt: String,
    val completedAt: String?,
    val dueAt: String,
    val isAnonymous: Boolean
)