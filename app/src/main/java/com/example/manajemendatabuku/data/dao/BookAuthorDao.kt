package com.example.manajemendatabuku.data.dao

import androidx.room.*
import com.example.manajemendatabuku.data.model.BookAuthor

/**
 * DAO untuk junction table BookAuthor (RAT)
 */
@Dao
interface BookAuthorDao {
    
    @Query("SELECT * FROM book_authors WHERE bookId = :bookId")
    suspend fun getAuthorsByBook(bookId: Long): List<BookAuthor>
    
    @Query("SELECT * FROM book_authors WHERE authorId = :authorId")
    suspend fun getBooksByAuthor(authorId: Long): List<BookAuthor>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookAuthor(bookAuthor: BookAuthor): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookAuthors(bookAuthors: List<BookAuthor>): List<Long>
    
    @Delete
    suspend fun deleteBookAuthor(bookAuthor: BookAuthor)
    
    @Query("DELETE FROM book_authors WHERE bookId = :bookId")
    suspend fun deleteAuthorsByBook(bookId: Long)
    
    @Query("DELETE FROM book_authors WHERE authorId = :authorId")
    suspend fun deleteBooksByAuthor(authorId: Long)
    
    @Query("SELECT COUNT(*) FROM book_authors WHERE bookId = :bookId AND authorId = :authorId")
    suspend fun exists(bookId: Long, authorId: Long): Boolean
}
