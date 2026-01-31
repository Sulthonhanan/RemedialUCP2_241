package com.example.manajemendatabuku.util

import com.example.manajemendatabuku.data.dao.CategoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class untuk mendeteksi cyclic reference pada struktur kategori
 * Mencegah perulangan tak terbatas yang dapat merusak logika pencarian
 */
object CyclicReferenceDetector {
    
    /**
     * Deteksi apakah mengubah parent category akan menyebabkan cyclic reference
     * 
     * @param categoryDao DAO untuk akses database
     * @param categoryId ID kategori yang akan diubah
     * @param newParentId ID parent baru yang akan di-assign
     * @return true jika akan terjadi cyclic reference, false jika aman
     */
    suspend fun wouldCreateCycle(
        categoryDao: CategoryDao,
        categoryId: Long,
        newParentId: Long?
    ): Boolean = withContext(Dispatchers.IO) {
        // Jika newParentId null, tidak akan terjadi cycle (menjadi root category)
        if (newParentId == null) {
            return@withContext false
        }
        
        // Jika newParentId sama dengan categoryId, akan terjadi cycle langsung
        if (newParentId == categoryId) {
            return@withContext true
        }
        
        // Gunakan query dari DAO untuk mengecek apakah categoryId ada dalam path parent dari newParentId
        return@withContext categoryDao.wouldCreateCycle(categoryId, newParentId)
    }
    
    /**
     * Deteksi cycle dalam seluruh struktur kategori menggunakan DFS
     * 
     * @param categoryDao DAO untuk akses database
     * @return List of category IDs yang terlibat dalam cycle, atau empty list jika tidak ada cycle
     */
    suspend fun detectCycles(
        categoryDao: CategoryDao
    ): List<Long> = withContext(Dispatchers.IO) {
        val allCategories = categoryDao.getAllCategories()
        val visited = mutableSetOf<Long>()
        val recStack = mutableSetOf<Long>()
        val cycles = mutableListOf<Long>()
        
        // Collect all categories first
        val categoriesList = mutableListOf<com.example.manajemendatabuku.data.model.Category>()
        allCategories.collect { categoriesList.addAll(it) }
        
        // Build adjacency map
        val adjacencyMap = mutableMapOf<Long, MutableList<Long>>()
        categoriesList.forEach { category ->
            if (category.parentCategoryId != null) {
                adjacencyMap.getOrPut(category.parentCategoryId!!) { mutableListOf() }
                    .add(category.categoryId)
            }
        }
        
        // DFS untuk deteksi cycle
        fun dfs(node: Long): Boolean {
            if (recStack.contains(node)) {
                cycles.add(node)
                return true // Cycle detected
            }
            if (visited.contains(node)) {
                return false
            }
            
            visited.add(node)
            recStack.add(node)
            
            adjacencyMap[node]?.forEach { child ->
                if (dfs(child)) {
                    cycles.add(node)
                    return true
                }
            }
            
            recStack.remove(node)
            return false
        }
        
        categoriesList.forEach { category ->
            if (!visited.contains(category.categoryId)) {
                dfs(category.categoryId)
            }
        }
        
        return@withContext cycles.distinct()
    }
}
