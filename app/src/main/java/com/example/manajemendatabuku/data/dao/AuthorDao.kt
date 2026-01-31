package com.example.manajemendatabuku.data.dao

import androidx.room.*
import com.example.manajemendatabuku.data.model.Author
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk operasi database pada tabel Author
 */
@Dao
interface AuthorDao {
    
    @Query("SELECT * FROM authors WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllAuthors(): Flow<List<Author>>
    
    @Query("SELECT * FROM authors WHERE authorId = :authorId AND isDeleted = 0")
    suspend fun getAuthorById(authorId: Long): Author?
    
    @Query("SELECT * FROM authors WHERE name LIKE '%' || :query || '%' AND isDeleted = 0")
    fun searchAuthors(query: String): Flow<List<Author>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: Author): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthors(authors: List<Author>): List<Long>
    
    @Update
    suspend fun updateAuthor(author: Author)
    
    @Delete
    suspend fun deleteAuthor(author: Author)
    
    @Query("UPDATE authors SET isDeleted = 1, deletedAt = :deletedAt WHERE authorId = :authorId")
    suspend fun softDeleteAuthor(authorId: Long, deletedAt: java.util.Date)
    
    @Query("SELECT * FROM authors WHERE isDeleted = 0 ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getAuthorsPaginated(limit: Int, offset: Int): List<Author>
}
