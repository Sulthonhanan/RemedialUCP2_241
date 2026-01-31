package com.example.manajemendatabuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manajemendatabuku.data.model.Category
import com.example.manajemendatabuku.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola state UI terkait Category
 */
class CategoryViewModel(private val repository: LibraryRepository) : ViewModel() {
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    private val _rootCategories = MutableStateFlow<List<Category>>(emptyList())
    val rootCategories: StateFlow<List<Category>> = _rootCategories.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadCategories()
        loadRootCategories()
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collect { categoryList ->
                _categories.value = categoryList
            }
        }
    }
    
    fun loadRootCategories() {
        viewModelScope.launch {
            repository.getRootCategories().collect { categoryList ->
                _rootCategories.value = categoryList
            }
        }
    }
    
    fun loadChildCategories(parentId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getChildCategories(parentId).collect { categoryList ->
                    _categories.value = categoryList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }
    
    fun insertCategory(category: Category, userId: String? = null, onResult: (Result<Long>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.insertCategory(category, userId)
            _isLoading.value = false
            
            result.onFailure { 
                _errorMessage.value = it.message
            }
            
            onResult(result)
        }
    }
    
    fun updateCategory(category: Category, userId: String? = null, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.updateCategory(category, userId)
            _isLoading.value = false
            
            result.onFailure { 
                _errorMessage.value = it.message
            }
            
            onResult(result)
        }
    }
    
    fun deleteCategory(
        categoryId: Long,
        deleteBooks: Boolean = false,
        userId: String? = null,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = repository.deleteCategory(categoryId, deleteBooks, userId)
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
