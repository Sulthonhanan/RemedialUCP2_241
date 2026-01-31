package com.example.manajemendatabuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.manajemendatabuku.data.repository.LibraryRepository

/**
 * Factory untuk membuat ViewModels dengan dependency injection
 */
class ViewModelFactory(private val repository: LibraryRepository) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BookViewModel::class.java) -> {
                BookViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                CategoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AuthorViewModel::class.java) -> {
                AuthorViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
