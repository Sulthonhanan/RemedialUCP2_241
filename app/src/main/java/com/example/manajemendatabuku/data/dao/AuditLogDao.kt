package com.example.manajemendatabuku.data.dao

import androidx.room.*
import com.example.manajemendatabuku.data.model.AuditLog
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk operasi database pada tabel AuditLog
 */
@Dao
interface AuditLogDao {
    
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLog>>
    
    @Query("SELECT * FROM audit_logs WHERE entityType = :entityType ORDER BY timestamp DESC")
    fun getLogsByEntityType(entityType: String): Flow<List<AuditLog>>
    
    @Query("SELECT * FROM audit_logs WHERE entityId = :entityId AND entityType = :entityType ORDER BY timestamp DESC")
    fun getLogsByEntity(entityType: String, entityId: Long): Flow<List<AuditLog>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLog): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<AuditLog>): List<Long>
    
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getLogsPaginated(limit: Int, offset: Int): List<AuditLog>
    
    @Query("DELETE FROM audit_logs WHERE timestamp < :beforeDate")
    suspend fun deleteOldLogs(beforeDate: java.util.Date)
}
