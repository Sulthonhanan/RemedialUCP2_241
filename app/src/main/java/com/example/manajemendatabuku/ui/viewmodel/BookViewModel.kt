package com.example.manajemendatabuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manajemendatabuku.data.model.Book
import com.example.manajemendatabuku.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola state UI terkait Book
 */
class BookViewModel(private val repository: LibraryRepository) : ViewModel() {
    
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()
    
    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadBooks()
    }
    
    fun loadBooks() {
        viewModelScope.launch {
            repository.getAllBooks().collect { bookList ->
                _books.value = bookList
            }
        }
    }
    
    fun loadBooksByCategory(categoryId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getBooksByCategoryRecursive(categoryId).collect { bookList ->
                    _books.value = bookList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun searchBooks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.searchBooks(query).collect { bookList ->
                    _books.value = bookList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun selectBook(book: Book) {
        _selectedBook.value = book
    }
    
    fun insertBook(book: Book, userId: String? = null, onResult: (Result<Long>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.insertBook(book, userId)
            _isLoading.value = false
            
            result.onFailure { 
                _errorMessage.value = it.message
            }
            
            onResult(result)
        }
    }
    
    fun updateBook(book: Book, userId: String? = null, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.updateBook(book, userId)
            _isLoading.value = false
            
            result.onFailure { 
                _errorMessage.value = it.message
            }
            
            onResult(result)
        }
    }
    
    fun deleteBook(bookId: Long, userId: String? = null, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.softDeleteBook(bookId, userId)
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
