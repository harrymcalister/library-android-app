package com.example.libdelivery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.libdelivery.database.book.BookDao
import com.example.libdelivery.database.library.LibraryDao

// Class for creating SharedViewModel passing both DAO as arguments
class SharedViewModelFactory(
    private val libraryDao: LibraryDao,
    private val bookDao: BookDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(libraryDao, bookDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}