package com.example.libdelivery.ui.viewmodel

import android.location.Location
import android.provider.Settings.Global
import android.util.Log
import androidx.lifecycle.*
import com.example.libdelivery.database.book.Book
import com.example.libdelivery.database.book.BookDao
import com.example.libdelivery.database.book.BookWithLibDetails
import com.example.libdelivery.database.library.Library
import com.example.libdelivery.database.library.LibraryDao
import com.example.libdelivery.utils.location.LocationService
import com.example.libdelivery.utils.location.LocationService.Companion.lastLocation
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.invokeOnCompletion
import kotlinx.coroutines.flow.Flow

// Status code used by BindingAdapters for when library API is implemented
enum class LibraryApiStatus { LOADING, ERROR, DONE }

class SharedViewModel(private val libraryDao: LibraryDao, private val bookDao: BookDao) : ViewModel() {
    /*
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LibraryApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<LibraryApiStatus> = _status


    private lateinit var _books: Flow<List<Book>>

    val books: Flow<List<Book>> = _books
    */

    // Hold a reference to the book we want to display in the BrowseDetailFragment
    private val _selectedBook = MutableLiveData<BookWithLibDetails>()
    val selectedBook: LiveData<BookWithLibDetails> = _selectedBook

    private val _selectedBookDistString = MutableLiveData<String>("")
    val selectedBookDistString: LiveData<String> = _selectedBookDistString

    // Used to know distances to each library
    val lastLocation: LiveData<Location> = LocationService.lastLocation


    fun allBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    fun allBooksWithLibName(): Flow<List<BookWithLibDetails>> = bookDao.getAllBooksWithLibDetails()

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
    fun setSelectedBook(book: BookWithLibDetails, distString: String) {
        _selectedBook.value = book
        _selectedBookDistString.value = distString
    }

    // Distance from lastLocation in km rounded to 1 decimal place for clarity
    fun formattedDistFromMyLocation(destLatitude: Double, destLongitude: Double): String {
        val endLocation = Location("end")
        endLocation.latitude = destLatitude
        endLocation.longitude = destLongitude

        val distInMetres = lastLocation.value?.distanceTo(endLocation)
        val distInKm = (distInMetres?.div(1000))

        // Return empty string if null so data binding expressions don't appear to the user
        // distInKm expected to be null before first location has been obtained
        val distString = if (distInKm != null) {
            "(%.1fkm away)".format(distInKm)
        } else {
            "( - km away)"
        }

        return distString
    }
}