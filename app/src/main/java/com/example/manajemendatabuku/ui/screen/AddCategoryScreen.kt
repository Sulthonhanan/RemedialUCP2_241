package com.example.manajemendatabuku.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.manajemendatabuku.data.database.LibraryDatabase
import com.example.manajemendatabuku.data.model.Category
import com.example.manajemendatabuku.data.repository.LibraryRepository
import com.example.manajemendatabuku.ui.viewmodel.CategoryViewModel
import com.example.manajemendatabuku.ui.viewmodel.ViewModelFactory

@Composable
fun AddCategoryScreen(navController: NavController) {
    val context = LocalContext.current
    val database = remember { LibraryDatabase.getDatabase(context) }
    val repository = remember { LibraryRepository(
        database.bookDao(),
        database.authorDao(),
        database.bookAuthorDao(),
        database.categoryDao(),
        database.auditLogDao()
    ) }
    val viewModel: CategoryViewModel = viewModel(factory = ViewModelFactory(repository))
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tambah Kategori",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Kategori *") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Deskripsi") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                val category = Category(
                    name = name,
                    description = if (description.isBlank()) null else description,
                    parentCategoryId = null // Root category
                )
                viewModel.insertCategory(category) { result ->
                    result.onSuccess {
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simpan")
        }
    }
}
