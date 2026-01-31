package com.example.manajemendatabuku.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.manajemendatabuku.data.database.LibraryDatabase
import com.example.manajemendatabuku.data.repository.LibraryRepository
import com.example.manajemendatabuku.ui.navigation.Screen
import com.example.manajemendatabuku.ui.viewmodel.BookViewModel
import com.example.manajemendatabuku.ui.viewmodel.ViewModelFactory

@Composable
fun BooksScreen(navController: NavController) {
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
    
    val books by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daftar Buku",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = { navController.navigate(Screen.AddBook.route) }) {
                Text("Tambah")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = errorMessage ?: "",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books) { book ->
                    BookItem(book = book)
                }
            }
        }
    }
}

@Composable
fun BookItem(book: com.example.manajemendatabuku.data.model.Book) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Physical ID: ${book.physicalId}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Status: ${book.status}",
                style = MaterialTheme.typography.bodySmall
            )
            if (book.isbn != null) {
                Text(
                    text = "ISBN: ${book.isbn}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
