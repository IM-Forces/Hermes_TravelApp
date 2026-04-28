package com.example.hermes_travelapp.data.repository

import com.example.hermes_travelapp.data.database.dao.AccessLogDao
import com.example.hermes_travelapp.data.database.entities.AccessLogEntity
import com.example.hermes_travelapp.domain.repository.AccessLogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessLogRepositoryImpl @Inject constructor(
    private val accessLogDao: AccessLogDao
) : AccessLogRepository {

    override suspend fun logAccess(userId: String, type: String) {
        val log = AccessLogEntity(
            userId = userId,
            type = type
        )
        accessLogDao.insertLog(log)
    }

    override suspend fun getLogsByUser(userId: String): List<AccessLogEntity> {
        return accessLogDao.getLogsByUser(userId)
    }
}
