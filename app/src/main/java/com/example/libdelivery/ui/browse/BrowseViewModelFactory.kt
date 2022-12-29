package com.example.libdelivery.ui.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.libdelivery.database.book.BookDao
import com.example.libdelivery.database.library.LibraryDao

// Class for creating BrowseViewModel passing both DAO as arguments
class BrowseViewModelFactory(
    private val libraryDao: LibraryDao,
    private val bookDao: BookDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrowseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BrowseViewModel(libraryDao, bookDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}