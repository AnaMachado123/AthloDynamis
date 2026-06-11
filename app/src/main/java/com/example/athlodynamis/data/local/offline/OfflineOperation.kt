package com.example.athlodynamis.data.local.offline

import kotlinx.serialization.Serializable

@Serializable
data class OfflineOperation(
    val id: String,
    val operationType: String,
    val entityName: String,
    val payloadJson: String,
    val status: String = "PENDING",
    val createdAt: Long = System.currentTimeMillis(),
    val syncedAt: Long? = null,
    val errorMessage: String? = null
)