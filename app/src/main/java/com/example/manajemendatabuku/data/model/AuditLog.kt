package com.example.manajemendatabuku.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity untuk Audit Log
 * Menyimpan rekaman data sebelum dan sesudah perubahan
 */
@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val logId: Long = 0,
    
    // Tipe entitas yang diubah
    val entityType: String, // "Book", "Author", "Category", "BookAuthor"
    
    // ID entitas yang diubah
    val entityId: Long,
    
    // Aksi yang dilakukan
    val action: String, // "INSERT", "UPDATE", "DELETE", "SOFT_DELETE"
    
    // Data sebelum perubahan (JSON string)
    val beforeData: String?,
    
    // Data sesudah perubahan (JSON string)
    val afterData: String?,
    
    // User yang melakukan perubahan
    val userId: String?,
    
    // Timestamp
    val timestamp: Date = Date()
)
