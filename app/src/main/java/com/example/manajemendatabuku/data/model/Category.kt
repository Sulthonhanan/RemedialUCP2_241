package com.example.manajemendatabuku.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity untuk kategori buku dengan struktur hierarkis
 * parentCategoryId dapat null untuk kategori root
 * Struktur ini mendukung kedalaman tak terbatas
 */
@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["parentCategoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["parentCategoryId"]), Index(value = ["name"])]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    
    val name: String,
    val description: String?,
    
    // Self-referential foreign key untuk hierarki
    val parentCategoryId: Long? = null,
    
    // Soft delete flag
    val isDeleted: Boolean = false,
    val deletedAt: Date? = null,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
