package com.example.manajemendatabuku.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity untuk buku fisik dengan ID unik
 * Setiap buku fisik memiliki status untuk tracking ketersediaan
 */
@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["categoryId"]), Index(value = ["physicalId"], unique = true)]
)
data class Book(
    @PrimaryKey(autoGenerate = true)
    val bookId: Long = 0,
    
    val title: String,
    val isbn: String? = null,
    val publisher: String? = null,
    val publishDate: Date? = null, // Memberikan nilai default null
    
    // ID unik untuk setiap buku fisik
    val physicalId: String,
    
    // Status buku: "available", "borrowed", "maintenance", "deleted"
    val status: String = "available",
    
    // Foreign key ke kategori (nullable untuk soft delete)
    val categoryId: Long? = null,
    
    // Soft delete flag
    val isDeleted: Boolean = false,
    val deletedAt: Date? = null,
    
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
