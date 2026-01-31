package com.example.manajemendatabuku.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.manajemendatabuku.ui.screen.*

/**
 * Navigation graph untuk aplikasi
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Books.route) {
            BooksScreen(navController = navController)
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(navController = navController)
        }
        composable(Screen.Authors.route) {
            AuthorsScreen(navController = navController)
        }
        composable(Screen.AddBook.route) {
            AddBookScreen(navController = navController)
        }
        composable(Screen.AddCategory.route) {
            AddCategoryScreen(navController = navController)
        }
        composable(Screen.AddAuthor.route) {
            AddAuthorScreen(navController = navController)
        }
    }
}

/**
 * Sealed class untuk mendefinisikan screens
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Books : Screen("books")
    object Categories : Screen("categories")
    object Authors : Screen("authors")
    object AddBook : Screen("add_book")
    object AddCategory : Screen("add_category")
    object AddAuthor : Screen("add_author")
}
