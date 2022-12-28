package com.example.libdelivery.ui.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.libdelivery.database.book.Book
import com.example.libdelivery.database.book.BookDao
import com.example.libdelivery.database.library.Library
import com.example.libdelivery.database.library.LibraryDao
import kotlinx.coroutines.flow.Flow

class BrowseViewModel(private val libraryDao: LibraryDao, private val bookDao: BookDao) : ViewModel() {

    fun allBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    fun allLibraries(): Flow<List<Library>> = libraryDao.getAllLibraries()

    fun searchBookTitle(title: String): Flow<List<Book>> = bookDao.getByBookTitle(title)

    fun searchLibraryName(name: String): Flow<List<Library>> = libraryDao.getByLibraryName(name)
}