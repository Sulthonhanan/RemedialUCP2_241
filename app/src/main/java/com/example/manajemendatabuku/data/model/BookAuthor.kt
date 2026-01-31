package com.example.manajemendatabuku.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Junction table (RAT - Relational Association Table) untuk relasi many-to-many
 * antara Book dan Author
 */
@Entity(
    tableName = "book_authors",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["bookId"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Author::class,
            parentColumns = ["authorId"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["bookId"]),
        Index(value = ["authorId"]),
        Index(value = ["bookId", "authorId"], unique = true)
    ]
)
data class BookAuthor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val bookId: Long,
    val authorId: Long
)
