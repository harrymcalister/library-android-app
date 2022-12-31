package com.example.libdelivery.ui.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libdelivery.database.book.Book
import com.example.libdelivery.database.book.BookDao
import com.example.libdelivery.database.book.BookWithLibName
import com.example.libdelivery.database.library.Library
import com.example.libdelivery.database.library.LibraryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// Status code used by BindingAdapters for when library API is implemented
enum class LibraryApiStatus { LOADING, ERROR, DONE }

class BrowseViewModel(private val libraryDao: LibraryDao, private val bookDao: BookDao) : ViewModel() {


    /*
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LibraryApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<LibraryApiStatus> = _status


    private lateinit var _books: Flow<List<Book>>

    val books: Flow<List<Book>> = _books
    */

    fun allBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    fun allBooksWithLibName(): Flow<List<BookWithLibName>> = bookDao.getAllBooksWithLibName()

    fun allLibraries(): Flow<List<Library>> = libraryDao.getAllLibraries()

    fun searchBookTitle(title: String): Flow<List<Book>> = bookDao.getByBookTitle(title)

    fun searchLibraryName(name: String): Flow<List<Library>> = libraryDao.getByLibraryName(name)

    /**
     * Gets book list information from the local database.
     * Should get data from the API once it has been implemented.
     * Once a larger database is used, architecture should be
     * modified to prevent loading of entire list of books.

    private fun getBooksList() {
        viewModelScope.launch {
            _status.value = LibraryApiStatus.LOADING
            try {
                _books = //retrieve data here
                _status.value = LibraryApiStatus.DONE
            } catch (e: Exception) {
                _status.value = LibraryApiStatus.ERROR
                _books = listOf()
            }
        }
    }
    */
}