package com.example.manajemendatabuku.data.dao

import androidx.room.*
import com.example.manajemendatabuku.data.model.Book
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk operasi database pada tabel Book
 */
@Dao
interface BookDao {
    
    @Query("SELECT * FROM books WHERE isDeleted = 0 ORDER BY title ASC")
    fun getAllBooks(): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE bookId = :bookId AND isDeleted = 0")
    suspend fun getBookById(bookId: Long): Book?
    
    @Query("SELECT * FROM books WHERE physicalId = :physicalId AND isDeleted = 0")
    suspend fun getBookByPhysicalId(physicalId: String): Book?
    
    @Query("SELECT * FROM books WHERE status = :status AND isDeleted = 0")
    fun getBooksByStatus(status: String): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE categoryId = :categoryId AND isDeleted = 0")
    fun getBooksByCategory(categoryId: Long): Flow<List<Book>>
    
    /**
     * Query rekursif untuk mendapatkan semua buku dalam kategori dan sub-kategorinya
     * Menggunakan CTE (Common Table Expression) untuk pencarian rekursif
     */
    @Query("""
        WITH RECURSIVE category_tree AS (
            -- Base case: kategori yang dicari
            SELECT categoryId, parentCategoryId, name
            FROM categories
            WHERE categoryId = :categoryId AND isDeleted = 0
            
            UNION ALL
            
            -- Recursive case: semua sub-kategori
            SELECT c.categoryId, c.parentCategoryId, c.name
            FROM categories c
            INNER JOIN category_tree ct ON c.parentCategoryId = ct.categoryId
            WHERE c.isDeleted = 0
        )
        SELECT b.* FROM books b
        INNER JOIN category_tree ct ON b.categoryId = ct.categoryId
        WHERE b.isDeleted = 0
        ORDER BY b.title ASC
    """)
    fun getBooksByCategoryRecursive(categoryId: Long): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' AND isDeleted = 0")
    fun searchBooks(query: String): Flow<List<Book>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>): List<Long>
    
    @Update
    suspend fun updateBook(book: Book)
    
    @Delete
    suspend fun deleteBook(book: Book)
    
    /**
     * Soft delete buku
     */
    @Query("UPDATE books SET isDeleted = 1, deletedAt = :deletedAt WHERE bookId = :bookId")
    suspend fun softDeleteBook(bookId: Long, deletedAt: java.util.Date)
    
    /**
     * Pindahkan buku ke kategori "Tanpa Kategori" (categoryId = NULL)
     */
    @Query("UPDATE books SET categoryId = NULL, updatedAt = :updatedAt WHERE categoryId = :categoryId AND isDeleted = 0")
    suspend fun moveBooksToNoCategory(categoryId: Long, updatedAt: java.util.Date)
    
    /**
     * Cek apakah ada buku dengan status "borrowed" dalam kategori
     */
    @Query("""
        WITH RECURSIVE category_tree AS (
            SELECT categoryId, parentCategoryId
            FROM categories
            WHERE categoryId = :categoryId AND isDeleted = 0
            
            UNION ALL
            
            SELECT c.categoryId, c.parentCategoryId
            FROM categories c
            INNER JOIN category_tree ct ON c.parentCategoryId = ct.categoryId
            WHERE c.isDeleted = 0
        )
        SELECT COUNT(*) > 0 FROM books b
        INNER JOIN category_tree ct ON b.categoryId = ct.categoryId
        WHERE b.status = 'borrowed' AND b.isDeleted = 0
    """)
    suspend fun hasBorrowedBooks(categoryId: Long): Boolean
    
    /**
     * Soft delete semua buku dalam kategori (rekursif)
     */
    @Transaction
    @Query("""
        WITH RECURSIVE category_tree AS (
            SELECT categoryId FROM categories
            WHERE categoryId = :categoryId AND isDeleted = 0
            
            UNION ALL
            
            SELECT c.categoryId FROM categories c
            INNER JOIN category_tree ct ON c.parentCategoryId = ct.categoryId
            WHERE c.isDeleted = 0
        )
        UPDATE books SET isDeleted = 1, deletedAt = :deletedAt
        WHERE categoryId IN (SELECT categoryId FROM category_tree) AND isDeleted = 0
    """)
    suspend fun softDeleteBooksInCategory(categoryId: Long, deletedAt: java.util.Date)
    
    /**
     * Query dengan pagination untuk performa
     */
    @Query("SELECT * FROM books WHERE isDeleted = 0 ORDER BY title ASC LIMIT :limit OFFSET :offset")
    suspend fun getBooksPaginated(limit: Int, offset: Int): List<Book>
}
