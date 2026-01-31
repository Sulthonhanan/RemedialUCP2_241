package com.example.manajemendatabuku.data.dao

import androidx.room.*
import com.example.manajemendatabuku.data.model.Category
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryDao {
    
    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE categoryId = :categoryId AND isDeleted = 0")
    suspend fun getCategoryById(categoryId: Long): Category?
    
    @Query("SELECT * FROM categories WHERE parentCategoryId IS NULL AND isDeleted = 0 ORDER BY name ASC")
    fun getRootCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE parentCategoryId = :parentId AND isDeleted = 0 ORDER BY name ASC")
    fun getChildCategories(parentId: Long): Flow<List<Category>>
    
    @Query("""
        WITH RECURSIVE category_tree AS (
            SELECT categoryId, parentCategoryId, name, description, createdAt, updatedAt, isDeleted, deletedAt
            FROM categories
            WHERE categoryId = :categoryId AND isDeleted = 0
            
            UNION ALL
            
            SELECT c.categoryId, c.parentCategoryId, c.name, c.description, c.createdAt, c.updatedAt, c.isDeleted, c.deletedAt
            FROM categories c
            INNER JOIN category_tree ct ON c.parentCategoryId = ct.categoryId
            WHERE c.isDeleted = 0
        )
        SELECT * FROM category_tree
        ORDER BY name ASC
    """)
    suspend fun getCategoryTree(categoryId: Long): List<Category>
    
    /**
     * Query rekursif untuk mendapatkan semua parent kategori dari suatu kategori
     */
    @Query("""
        WITH RECURSIVE category_path AS (
            SELECT categoryId, parentCategoryId, name, description, createdAt, updatedAt, isDeleted, deletedAt
            FROM categories
            WHERE categoryId = :categoryId AND isDeleted = 0
            
            UNION ALL
            
            SELECT c.categoryId, c.parentCategoryId, c.name, c.description, c.createdAt, c.updatedAt, c.isDeleted, c.deletedAt
            FROM categories c
            INNER JOIN category_path cp ON c.categoryId = cp.parentCategoryId
            WHERE c.isDeleted = 0
        )
        SELECT * FROM category_path
        ORDER BY categoryId ASC
    """)
    suspend fun getCategoryPath(categoryId: Long): List<Category>
    
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :query || '%' AND isDeleted = 0")
    fun searchCategories(query: String): Flow<List<Category>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>): List<Long>
    
    @Update
    suspend fun updateCategory(category: Category)
    
    @Delete
    suspend fun deleteCategory(category: Category)
    
    @Query("UPDATE categories SET isDeleted = 1, deletedAt = :deletedAt WHERE categoryId = :categoryId")
    suspend fun softDeleteCategory(categoryId: Long, deletedAt: java.util.Date)
    
    /**
     * Soft delete kategori dan semua sub-kategorinya secara rekursif
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
        UPDATE categories SET isDeleted = 1, deletedAt = :deletedAt
        WHERE categoryId IN (SELECT categoryId FROM category_tree) AND isDeleted = 0
    """)
    suspend fun softDeleteCategoryTree(categoryId: Long, deletedAt: java.util.Date)
    
    /**
     * Cek apakah kategori memiliki parent yang akan menyebabkan cyclic reference
     * Jika categoryId ada dalam path parent dari newParentId, maka akan terjadi cycle
     */
    @Query("""
        WITH RECURSIVE category_path AS (
            SELECT categoryId, parentCategoryId
            FROM categories
            WHERE categoryId = :newParentId AND isDeleted = 0
            
            UNION ALL
            
            SELECT c.categoryId, c.parentCategoryId
            FROM categories c
            INNER JOIN category_path cp ON c.categoryId = cp.parentCategoryId
            WHERE c.isDeleted = 0
        )
        SELECT COUNT(*) > 0 FROM category_path WHERE categoryId = :categoryId
    """)
    suspend fun wouldCreateCycle(categoryId: Long, newParentId: Long): Boolean
    
    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getCategoriesPaginated(limit: Int, offset: Int): List<Category>
}
