package com.example.libdelivery.ui.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.example.libdelivery.database.book.Book
import com.example.libdelivery.database.book.BookDao
import com.example.libdelivery.database.book.BookWithLibDetails
import com.example.libdelivery.database.library.Library
import com.example.libdelivery.database.library.LibraryDao
import com.example.libdelivery.utils.location.LocationService
import com.example.libdelivery.utils.location.LocationService.Companion.lastLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Status code used by BindingAdapters for when library API is implemented
// enum class LibraryApiStatus { LOADING, ERROR, DONE }

class SharedViewModel(private val libraryDao: LibraryDao, private val bookDao: BookDao) : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    // private val _status = MutableLiveData<LibraryApiStatus>()

    // The external immutable LiveData for the request status
    // val status: LiveData<LibraryApiStatus> = _status

    // Holds the currently selected book subject filter, null if no filter applied
    private val _subjectFilter = MutableLiveData<String?>()
    val subjectFilter: LiveData<String?> = _subjectFilter

    // Holds the currently selected library distance filter, null if no filter applied
    private val _distanceFilter = MutableLiveData<Int?>()
    val distanceFilter: LiveData<Int?> = _distanceFilter

    // Holds the search query associated with current list of books displayed
    // in BrowseScrollFragment, used to persist book list on fragment change
    private val _lastQuery = MutableLiveData<String>("")
    val lastQuery: LiveData<String> = _lastQuery

    // Holds the current list of books displayed in BrowseScrollFragment
    private val _books = MutableLiveData<List<BookWithLibDetails>>()
    val books: LiveData<List<BookWithLibDetails>> = _books

    // Hold a reference to the book we want to display in the BrowseDetailFragment
    private val _selectedBook = MutableLiveData<BookWithLibDetails>()
    val selectedBook: LiveData<BookWithLibDetails> = _selectedBook

    private val _selectedBookDistString = MutableLiveData<String>("")
    val selectedBookDistString: LiveData<String> = _selectedBookDistString

    // Used to know distances to each library
    val lastLocation: LiveData<Location> = LocationService.lastLocation

    init {
        viewModelScope.launch {
            performDatabaseQuery(::allBooksWithLibName)
        }
    }

    fun allBooks(): List<Book> = bookDao.getAllBooks()

    fun allBooksWithLibName(): List<BookWithLibDetails> = bookDao.getAllBooksWithLibDetails()

    fun searchBooksWithKeyword(searchQuery: String): List<BookWithLibDetails> = bookDao.searchBooksWithLibDetails(searchQuery)

    fun allBooksWithSubject(subject: String): List<BookWithLibDetails> = bookDao.getAllBooksWithSubject(subject)

    fun searchBooksWithKeywordAndSubject(searchQuery: String, subject: String): List<BookWithLibDetails> = bookDao.searchBooksWithSubject(searchQuery, subject)

    fun allLibraries(): List<Library> = libraryDao.getAllLibraries()

    fun searchBookTitle(title: String): List<Book> = bookDao.getByBookTitle(title)

    fun searchLibraryName(name: String): List<Library> = libraryDao.getByLibraryName(name)

    /**
     * Use this method get data from an API once it has been implemented.
     * Once a larger database is used, architecture should be
     * modified to prevent loading of entire list of books.
     */
    /*
    private fun getBooksList() {
        viewModelScope.launch(Dispatchers.IO) {
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

    // Search query can be an empty string but not nullable
    // Null subject filter means no filter has been applied
    fun generateCorrectDaoQuery(currentSearchQuery: String, subjectFilter: String?):
        () -> List<BookWithLibDetails> {
            // If query is not an empty string, perform a query+subject search
            // as long as a subject filter is active.
            if (currentSearchQuery != "") {
                if (subjectFilter != null) {
                    return { searchBooksWithKeywordAndSubject(currentSearchQuery, subjectFilter) }
                } else {
                    // Here there is no subject filter selected
                    return { searchBooksWithKeyword(currentSearchQuery) }
                }
            }
            // If query is an empty string, perform a search using just the subject filter
            else {
                if (subjectFilter != null) {
                    return { allBooksWithSubject(subjectFilter) }
                } else {
                    // Here there is no subject filter selected, so just search all books
                    return { allBooksWithLibName() }
                }
            }
    }

    fun performDatabaseQuery(daoQuery: () -> List<BookWithLibDetails>) {
        viewModelScope.launch(Dispatchers.IO) {
            val bookList = daoQuery()
            withContext(Dispatchers.Main) {
                _books.value = bookList
            }
        }
    }

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

    fun setLastQuery(query: String) {
        _lastQuery.value = query
    }

    fun setDistanceFilter(distance: Int?) {
        _distanceFilter.value = distance
    }

    fun setSubjectFilter(subject: String?) {
        _subjectFilter.value = subject
        val appropriateDaoQuery = generateCorrectDaoQuery(_lastQuery.value!!, subjectFilter.value)
        performDatabaseQuery(daoQuery = appropriateDaoQuery)
    }
}