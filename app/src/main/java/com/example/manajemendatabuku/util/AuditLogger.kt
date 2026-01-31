package com.example.manajemendatabuku.util

import com.example.manajemendatabuku.data.dao.AuditLogDao
import com.example.manajemendatabuku.data.model.AuditLog
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Utility class untuk audit logging
 * Menyimpan rekaman data sebelum dan sesudah perubahan
 */
object AuditLogger {
    
    private val gson = Gson()
    
    /**
     * Log operasi INSERT
     */
    suspend fun logInsert(
        auditLogDao: AuditLogDao,
        entityType: String,
        entityId: Long,
        afterData: Any,
        userId: String? = null
    ) = withContext(Dispatchers.IO) {
        val log = AuditLog(
            entityType = entityType,
            entityId = entityId,
            action = "INSERT",
            beforeData = null,
            afterData = gson.toJson(afterData),
            userId = userId,
            timestamp = Date()
        )
        auditLogDao.insertLog(log)
    }
    
    /**
     * Log operasi UPDATE
     */
    suspend fun logUpdate(
        auditLogDao: AuditLogDao,
        entityType: String,
        entityId: Long,
        beforeData: Any,
        afterData: Any,
        userId: String? = null
    ) = withContext(Dispatchers.IO) {
        val log = AuditLog(
            entityType = entityType,
            entityId = entityId,
            action = "UPDATE",
            beforeData = gson.toJson(beforeData),
            afterData = gson.toJson(afterData),
            userId = userId,
            timestamp = Date()
        )
        auditLogDao.insertLog(log)
    }
    
    /**
     * Log operasi DELETE (hard delete)
     */
    suspend fun logDelete(
        auditLogDao: AuditLogDao,
        entityType: String,
        entityId: Long,
        beforeData: Any,
        userId: String? = null
    ) = withContext(Dispatchers.IO) {
        val log = AuditLog(
            entityType = entityType,
            entityId = entityId,
            action = "DELETE",
            beforeData = gson.toJson(beforeData),
            afterData = null,
            userId = userId,
            timestamp = Date()
        )
        auditLogDao.insertLog(log)
    }
    
    /**
     * Log operasi SOFT_DELETE
     */
    suspend fun logSoftDelete(
        auditLogDao: AuditLogDao,
        entityType: String,
        entityId: Long,
        beforeData: Any,
        userId: String? = null
    ) = withContext(Dispatchers.IO) {
        val log = AuditLog(
            entityType = entityType,
            entityId = entityId,
            action = "SOFT_DELETE",
            beforeData = gson.toJson(beforeData),
            afterData = null,
            userId = userId,
            timestamp = Date()
        )
        auditLogDao.insertLog(log)
    }
}
