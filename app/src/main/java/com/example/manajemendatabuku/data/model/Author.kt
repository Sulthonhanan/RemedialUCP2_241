package com.example.manajemendatabuku.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity untuk pengarang buku
 */
@Entity(tableName = "authors")
data class Author(
    @PrimaryKey(autoGenerate = true)
    val authorId: Long = 0,
    
    val name: String,
    val email: String?,
    val biography: String?,
    
    // Soft delete flag
    val isDeleted: Boolean = false,
    val deletedAt: Date? = null,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
