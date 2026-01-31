package com.example.manajemendatabuku.data.database

import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Helper untuk menjalankan database transaction
 * Memastikan atomicity operasi kompleks
 */
class DatabaseTransactionHelper(private val database: LibraryDatabase) {
    
    /**
     * Execute block dalam transaction
     * Jika terjadi exception, semua perubahan akan di-rollback
     */
    suspend fun <T> withTransaction(block: suspend () -> T): T = withContext(Dispatchers.IO) {
        database.withTransaction {
            block()
        }
    }
}
