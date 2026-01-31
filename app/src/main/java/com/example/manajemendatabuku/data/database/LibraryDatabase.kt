package com.example.manajemendatabuku.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.manajemendatabuku.data.dao.*
import com.example.manajemendatabuku.data.model.*

/**
 * Room Database untuk Sistem Manajemen Data Buku
 */
@Database(
    entities = [
        Book::class,
        Author::class,
        BookAuthor::class,
        Category::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LibraryDatabase : RoomDatabase() {
    
    abstract fun bookDao(): BookDao
    abstract fun authorDao(): AuthorDao
    abstract fun bookAuthorDao(): BookAuthorDao
    abstract fun categoryDao(): CategoryDao
    abstract fun auditLogDao(): AuditLogDao
    
    companion object {
        @Volatile
        private var INSTANCE: LibraryDatabase? = null
        
        fun getDatabase(context: Context): LibraryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibraryDatabase::class.java,
                    "library_database"
                )
                    .fallbackToDestructiveMigration() // Untuk development, ganti dengan proper migration di production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
