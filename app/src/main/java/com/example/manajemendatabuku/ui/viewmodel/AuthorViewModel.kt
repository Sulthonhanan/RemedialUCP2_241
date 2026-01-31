package com.example.manajemendatabuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manajemendatabuku.data.model.Author
import com.example.manajemendatabuku.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola state UI terkait Author
 */
class AuthorViewModel(private val repository: LibraryRepository) : ViewModel() {
    
    private val _authors = MutableStateFlow<List<Author>>(emptyList())
    val authors: StateFlow<List<Author>> = _authors.asStateFlow()
    
    private val _selectedAuthor = MutableStateFlow<Author?>(null)
    val selectedAuthor: StateFlow<Author?> = _selectedAuthor.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadAuthors()
    }
    
    fun loadAuthors() {
        viewModelScope.launch {
            repository.getAllAuthors().collect { authorList ->
                _authors.value = authorList
            }
        }
    }
    
    fun selectAuthor(author: Author) {
        _selectedAuthor.value = author
    }
    
    fun insertAuthor(author: Author, userId: String? = null, onResult: (Result<Long>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.insertAuthor(author, userId)
            _isLoading.value = false
            
            result.onFailure { 
                _errorMessage.value = it.message
            }
            
            onResult(result)
        }
    }
    
    fun updateAuthor(author: Author, userId: String? = null, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.updateAuthor(author, userId)
            _isLoading.value = false
            
            result.onFailure { 
                _errorMessage.value = it.message
            }
            
            onResult(result)
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
