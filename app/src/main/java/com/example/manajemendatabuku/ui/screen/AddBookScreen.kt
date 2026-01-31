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
import com.example.manajemendatabuku.data.model.Book
import com.example.manajemendatabuku.data.repository.LibraryRepository
import com.example.manajemendatabuku.ui.viewmodel.BookViewModel
import com.example.manajemendatabuku.ui.viewmodel.ViewModelFactory
import java.util.Date

@Composable
fun AddBookScreen(navController: NavController) {
    val context = LocalContext.current
    val database = remember { LibraryDatabase.getDatabase(context) }
    val repository = remember { LibraryRepository(
        database.bookDao(),
        database.authorDao(),
        database.bookAuthorDao(),
        database.categoryDao(),
        database.auditLogDao()
    ) }
    val viewModel: BookViewModel = viewModel(factory = ViewModelFactory(repository))
    
    var title by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var physicalId by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tambah Buku",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Judul Buku *") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = physicalId,
            onValueChange = { physicalId = it },
            label = { Text("Physical ID *") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = isbn,
            onValueChange = { isbn = it },
            label = { Text("ISBN") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = publisher,
            onValueChange = { publisher = it },
            label = { Text("Penerbit") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                val book = Book(
                    title = title,
                    isbn = if (isbn.isBlank()) null else isbn,
                    publisher = if (publisher.isBlank()) null else publisher,
                    publishDate = null, // Tambahkan parameter ini
                    physicalId = physicalId,
                    categoryId = null,
                    status = "available"
                )
                viewModel.insertBook(book) { result ->
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
