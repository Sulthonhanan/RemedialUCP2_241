package com.example.manajemendatabuku.util

import java.util.Date
import java.util.regex.Pattern

/**
 * Utility class untuk validasi data berdasarkan tipe
 * Mencegah kesalahan entri dengan validasi ketat
 */
object ValidationUtils {
    
    /**
     * Validasi title buku
     */
    fun validateBookTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult(false, "Judul buku tidak boleh kosong")
            title.length < 3 -> ValidationResult(false, "Judul buku minimal 3 karakter")
            title.length > 200 -> ValidationResult(false, "Judul buku maksimal 200 karakter")
            else -> ValidationResult(true, "Valid")
        }
    }
    
    /**
     * Validasi ISBN
     */
    fun validateISBN(isbn: String?): ValidationResult {
        if (isbn == null || isbn.isBlank()) {
            return ValidationResult(true, "Valid") // ISBN optional
        }
        
        // Format ISBN: 10 atau 13 digit, boleh dengan hyphen
        val cleanISBN = isbn.replace("-", "").replace(" ", "")
        val isbn10Pattern = Pattern.compile("^[0-9]{10}$")
        val isbn13Pattern = Pattern.compile("^[0-9]{13}$")
        
        return when {
            isbn10Pattern.matcher(cleanISBN).matches() || isbn13Pattern.matcher(cleanISBN).matches() -> {
                ValidationResult(true, "Valid")
            }
            else -> ValidationResult(false, "Format ISBN tidak valid (harus 10 atau 13 digit)")
        }
    }
    
    /**
     * Validasi Physical ID (ID unik untuk setiap buku fisik)
     */
    fun validatePhysicalId(physicalId: String): ValidationResult {
        return when {
            physicalId.isBlank() -> ValidationResult(false, "Physical ID tidak boleh kosong")
            physicalId.length < 5 -> ValidationResult(false, "Physical ID minimal 5 karakter")
            physicalId.length > 50 -> ValidationResult(false, "Physical ID maksimal 50 karakter")
            !physicalId.matches(Regex("^[A-Za-z0-9_-]+$")) -> {
                ValidationResult(false, "Physical ID hanya boleh mengandung huruf, angka, underscore, dan hyphen")
            }
            else -> ValidationResult(true, "Valid")
        }
    }
    
    /**
     * Validasi status buku
     */
    fun validateBookStatus(status: String): ValidationResult {
        val validStatuses = listOf("available", "borrowed", "maintenance", "deleted")
        return if (validStatuses.contains(status.lowercase())) {
            ValidationResult(true, "Valid")
        } else {
            ValidationResult(false, "Status tidak valid. Harus salah satu dari: ${validStatuses.joinToString(", ")}")
        }
    }
    
    /**
     * Validasi nama pengarang
     */
    fun validateAuthorName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Nama pengarang tidak boleh kosong")
            name.length < 2 -> ValidationResult(false, "Nama pengarang minimal 2 karakter")
            name.length > 100 -> ValidationResult(false, "Nama pengarang maksimal 100 karakter")
            !name.matches(Regex("^[A-Za-z\\s.\\-']+$")) -> {
                ValidationResult(false, "Nama pengarang hanya boleh mengandung huruf, spasi, titik, hyphen, dan apostrof")
            }
            else -> ValidationResult(true, "Valid")
        }
    }
    
    /**
     * Validasi email pengarang
     */
    fun validateEmail(email: String?): ValidationResult {
        if (email == null || email.isBlank()) {
            return ValidationResult(true, "Valid") // Email optional
        }
        
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            Pattern.CASE_INSENSITIVE
        )
        
        return if (emailPattern.matcher(email).matches()) {
            ValidationResult(true, "Valid")
        } else {
            ValidationResult(false, "Format email tidak valid")
        }
    }
    
    /**
     * Validasi nama kategori
     */
    fun validateCategoryName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Nama kategori tidak boleh kosong")
            name.length < 2 -> ValidationResult(false, "Nama kategori minimal 2 karakter")
            name.length > 100 -> ValidationResult(false, "Nama kategori maksimal 100 karakter")
            else -> ValidationResult(true, "Valid")
        }
    }
    
    /**
     * Validasi tanggal publish
     */
    fun validatePublishDate(date: Date?): ValidationResult {
        if (date == null) {
            return ValidationResult(true, "Valid") // Date optional
        }
        
        val now = Date()
        return if (date.after(now)) {
            ValidationResult(false, "Tanggal publish tidak boleh di masa depan")
        } else {
            ValidationResult(true, "Valid")
        }
    }
    
    /**
     * Validasi ID numerik
     */
    fun validateId(id: Long?): ValidationResult {
        return if (id == null || id <= 0) {
            ValidationResult(false, "ID tidak valid")
        } else {
            ValidationResult(true, "Valid")
        }
    }
}

/**
 * Data class untuk hasil validasi
 */
data class ValidationResult(
    val isValid: Boolean,
    val message: String
)
