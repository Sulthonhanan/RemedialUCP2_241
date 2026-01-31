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
import com.example.manajemendatabuku.data.model.Author
import com.example.manajemendatabuku.data.repository.LibraryRepository
import com.example.manajemendatabuku.ui.viewmodel.AuthorViewModel
import com.example.manajemendatabuku.ui.viewmodel.ViewModelFactory

@Composable
fun AddAuthorScreen(navController: NavController) {
    val context = LocalContext.current
    val database = remember { LibraryDatabase.getDatabase(context) }
    val repository = remember { LibraryRepository(
        database.bookDao(),
        database.authorDao(),
        database.bookAuthorDao(),
        database.categoryDao(),
        database.auditLogDao()
    ) }
    val viewModel: AuthorViewModel = viewModel(factory = ViewModelFactory(repository))
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var biography by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tambah Pengarang",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Pengarang *") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = biography,
            onValueChange = { biography = it },
            label = { Text("Biografi") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                val author = Author(
                    name = name,
                    email = if (email.isBlank()) null else email,
                    biography = if (biography.isBlank()) null else biography
                )
                viewModel.insertAuthor(author) { result ->
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
