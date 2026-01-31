package com.example.manajemendatabuku.data.repository

import com.example.manajemendatabuku.data.dao.*
import com.example.manajemendatabuku.data.model.*
import com.example.manajemendatabuku.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Repository layer untuk mengelola operasi database
 * Mengimplementasikan business logic kompleks termasuk transaksi dan validasi
 */
class LibraryRepository(
    private val bookDao: BookDao,
    private val authorDao: AuthorDao,
    private val bookAuthorDao: BookAuthorDao,
    private val categoryDao: CategoryDao,
    private val auditLogDao: AuditLogDao
) {
    
    // ==================== BOOK OPERATIONS ====================
    
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks().flowOn(Dispatchers.IO)
    
    suspend fun getBookById(bookId: Long): Book? = withContext(Dispatchers.IO) {
        bookDao.getBookById(bookId)
    }
    
    fun getBooksByCategory(categoryId: Long): Flow<List<Book>> = 
        bookDao.getBooksByCategory(categoryId).flowOn(Dispatchers.IO)
    
    fun getBooksByCategoryRecursive(categoryId: Long): Flow<List<Book>> = 
        bookDao.getBooksByCategoryRecursive(categoryId).flowOn(Dispatchers.IO)
    
    fun searchBooks(query: String): Flow<List<Book>> = 
        bookDao.searchBooks(query).flowOn(Dispatchers.IO)
    
    suspend fun insertBook(book: Book, userId: String? = null): Result<Long> = withContext(Dispatchers.IO) {
        try {
            // Validasi
            val titleValidation = ValidationUtils.validateBookTitle(book.title)
            if (!titleValidation.isValid) {
                return@withContext Result.failure(Exception(titleValidation.message))
            }
            
            val physicalIdValidation = ValidationUtils.validatePhysicalId(book.physicalId)
            if (!physicalIdValidation.isValid) {
                return@withContext Result.failure(Exception(physicalIdValidation.message))
            }
            
            val isbnValidation = ValidationUtils.validateISBN(book.isbn)
            if (!isbnValidation.isValid) {
                return@withContext Result.failure(Exception(isbnValidation.message))
            }
            
            val statusValidation = ValidationUtils.validateBookStatus(book.status)
            if (!statusValidation.isValid) {
                return@withContext Result.failure(Exception(statusValidation.message))
            }
            
            // Cek duplikasi physicalId
            val existing = bookDao.getBookByPhysicalId(book.physicalId)
            if (existing != null && !existing.isDeleted) {
                return@withContext Result.failure(Exception("Physical ID sudah digunakan"))
            }
            
            val bookId = bookDao.insertBook(book)
            
            // Audit log
            AuditLogger.logInsert(auditLogDao, "Book", bookId, book, userId)
            
            Result.success(bookId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateBook(book: Book, userId: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = bookDao.getBookById(book.bookId)
                ?: return@withContext Result.failure(Exception("Buku tidak ditemukan"))
            
            // Validasi
            val titleValidation = ValidationUtils.validateBookTitle(book.title)
            if (!titleValidation.isValid) {
                return@withContext Result.failure(Exception(titleValidation.message))
            }
            
            val updatedBook = book.copy(updatedAt = Date())
            bookDao.updateBook(updatedBook)
            
            // Audit log
            AuditLogger.logUpdate(auditLogDao, "Book", book.bookId, existing, updatedBook, userId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun softDeleteBook(bookId: Long, userId: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val book = bookDao.getBookById(bookId)
                ?: return@withContext Result.failure(Exception("Buku tidak ditemukan"))
            
            val deletedAt = Date()
            bookDao.softDeleteBook(bookId, deletedAt)
            
            // Audit log
            AuditLogger.logSoftDelete(auditLogDao, "Book", bookId, book, userId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== AUTHOR OPERATIONS ====================
    
    fun getAllAuthors(): Flow<List<Author>> = authorDao.getAllAuthors().flowOn(Dispatchers.IO)
    
    suspend fun insertAuthor(author: Author, userId: String? = null): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val nameValidation = ValidationUtils.validateAuthorName(author.name)
            if (!nameValidation.isValid) {
                return@withContext Result.failure(Exception(nameValidation.message))
            }
            
            val emailValidation = ValidationUtils.validateEmail(author.email)
            if (!emailValidation.isValid) {
                return@withContext Result.failure(Exception(emailValidation.message))
            }
            
            val authorId = authorDao.insertAuthor(author)
            
            // Audit log
            AuditLogger.logInsert(auditLogDao, "Author", authorId, author, userId)
            
            Result.success(authorId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateAuthor(author: Author, userId: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = authorDao.getAuthorById(author.authorId)
                ?: return@withContext Result.failure(Exception("Pengarang tidak ditemukan"))
            
            val nameValidation = ValidationUtils.validateAuthorName(author.name)
            if (!nameValidation.isValid) {
                return@withContext Result.failure(Exception(nameValidation.message))
            }
            
            val updatedAuthor = author.copy(updatedAt = Date())
            authorDao.updateAuthor(updatedAuthor)
            
            // Audit log
            AuditLogger.logUpdate(auditLogDao, "Author", author.authorId, existing, updatedAuthor, userId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== BOOK-AUTHOR RELATIONSHIP ====================
    
    suspend fun addAuthorToBook(bookId: Long, authorId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val exists = bookAuthorDao.exists(bookId, authorId)
            if (exists) {
                return@withContext Result.failure(Exception("Relasi sudah ada"))
            }
            
            val bookAuthor = BookAuthor(bookId = bookId, authorId = authorId)
            bookAuthorDao.insertBookAuthor(bookAuthor)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeAuthorFromBook(bookId: Long, authorId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val bookAuthors = bookAuthorDao.getAuthorsByBook(bookId)
            val bookAuthor = bookAuthors.find { it.authorId == authorId }
                ?: return@withContext Result.failure(Exception("Relasi tidak ditemukan"))
            
            bookAuthorDao.deleteBookAuthor(bookAuthor)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== CATEGORY OPERATIONS ====================
    
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories().flowOn(Dispatchers.IO)
    
    fun getRootCategories(): Flow<List<Category>> = categoryDao.getRootCategories().flowOn(Dispatchers.IO)
    
    fun getChildCategories(parentId: Long): Flow<List<Category>> = 
        categoryDao.getChildCategories(parentId).flowOn(Dispatchers.IO)
    
    suspend fun insertCategory(category: Category, userId: String? = null): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val nameValidation = ValidationUtils.validateCategoryName(category.name)
            if (!nameValidation.isValid) {
                return@withContext Result.failure(Exception(nameValidation.message))
            }
            
            // Deteksi cyclic reference
            if (category.parentCategoryId != null) {
                val wouldCycle = CyclicReferenceDetector.wouldCreateCycle(
                    categoryDao,
                    category.categoryId,
                    category.parentCategoryId
                )
                if (wouldCycle) {
                    return@withContext Result.failure(Exception("Akan terjadi cyclic reference"))
                }
            }
            
            val categoryId = categoryDao.insertCategory(category)
            
            // Audit log
            AuditLogger.logInsert(auditLogDao, "Category", categoryId, category, userId)
            
            Result.success(categoryId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCategory(category: Category, userId: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = categoryDao.getCategoryById(category.categoryId)
                ?: return@withContext Result.failure(Exception("Kategori tidak ditemukan"))
            
            val nameValidation = ValidationUtils.validateCategoryName(category.name)
            if (!nameValidation.isValid) {
                return@withContext Result.failure(Exception(nameValidation.message))
            }
            
            // Deteksi cyclic reference jika parent berubah
            if (category.parentCategoryId != existing.parentCategoryId && category.parentCategoryId != null) {
                val wouldCycle = CyclicReferenceDetector.wouldCreateCycle(
                    categoryDao,
                    category.categoryId,
                    category.parentCategoryId
                )
                if (wouldCycle) {
                    return@withContext Result.failure(Exception("Akan terjadi cyclic reference"))
                }
            }
            
            val updatedCategory = category.copy(updatedAt = Date())
            categoryDao.updateCategory(updatedCategory)
            
            // Audit log
            AuditLogger.logUpdate(auditLogDao, "Category", category.categoryId, existing, updatedCategory, userId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Kompleks deletion logic untuk kategori dengan transaction
     * - Jika ada buku dengan status "borrowed" → rollback
     * - Jika tidak ada buku borrowed → opsi: soft delete buku atau pindah ke "Tanpa Kategori"
     */
    suspend fun deleteCategory(
        categoryId: Long,
        deleteBooks: Boolean = false,
        userId: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        // Gunakan database transaction untuk memastikan atomicity
        // Note: Room akan otomatis rollback jika terjadi exception
        try {
            val category = categoryDao.getCategoryById(categoryId)
                ?: return@withContext Result.failure(Exception("Kategori tidak ditemukan"))
            
            // Cek apakah ada buku dengan status "borrowed" dalam kategori (rekursif)
            val hasBorrowed = bookDao.hasBorrowedBooks(categoryId)
            
            if (hasBorrowed) {
                // Rollback: tidak boleh menghapus kategori dengan buku borrowed
                // Exception akan menyebabkan Room melakukan rollback otomatis
                return@withContext Result.failure(
                    Exception("Tidak dapat menghapus kategori karena masih ada buku dengan status 'dipinjam'. Operasi dibatalkan.")
                )
            }
            
            val now = Date()
            
            // Jika deleteBooks = true, soft delete semua buku dalam kategori
            // Jika false, pindahkan buku ke "Tanpa Kategori" (categoryId = NULL)
            if (deleteBooks) {
                bookDao.softDeleteBooksInCategory(categoryId, now)
            } else {
                bookDao.moveBooksToNoCategory(categoryId, now)
            }
            
            // Soft delete kategori dan semua sub-kategorinya
            categoryDao.softDeleteCategoryTree(categoryId, now)
            
            // Audit log
            AuditLogger.logSoftDelete(auditLogDao, "Category", categoryId, category, userId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            // Room akan otomatis rollback semua perubahan jika terjadi exception
            Result.failure(e)
        }
    }
    
    // ==================== AUDIT LOG OPERATIONS ====================
    
    fun getAllAuditLogs(): Flow<List<AuditLog>> = auditLogDao.getAllLogs().flowOn(Dispatchers.IO)
    
    fun getAuditLogsByEntity(entityType: String, entityId: Long): Flow<List<AuditLog>> = 
        auditLogDao.getLogsByEntity(entityType, entityId).flowOn(Dispatchers.IO)
    
    // ==================== PAGINATION ====================
    
    suspend fun getBooksPaginated(limit: Int, offset: Int): List<Book> = withContext(Dispatchers.IO) {
        bookDao.getBooksPaginated(limit, offset)
    }
    
    suspend fun getAuthorsPaginated(limit: Int, offset: Int): List<Author> = withContext(Dispatchers.IO) {
        authorDao.getAuthorsPaginated(limit, offset)
    }
    
    suspend fun getCategoriesPaginated(limit: Int, offset: Int): List<Category> = withContext(Dispatchers.IO) {
        categoryDao.getCategoriesPaginated(limit, offset)
    }
}
