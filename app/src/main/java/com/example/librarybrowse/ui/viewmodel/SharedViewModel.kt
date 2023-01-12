package com.example.librarybrowse.ui.viewmodel

import android.location.Location
import androidx.lifecycle.*
import com.example.librarybrowse.database.book.Book
import com.example.librarybrowse.database.book.BookDao
import com.example.librarybrowse.database.book.BookWithLibDetails
import com.example.librarybrowse.database.library.Library
import com.example.librarybrowse.database.library.LibraryDao
import com.example.librarybrowse.utils.location.LocationService
import kotlinx.coroutines.*
import kotlin.collections.MutableList


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

    private val _selectedBookDistance = MutableLiveData<Float?>()
    val selectedBookDistance: LiveData<Float?> = _selectedBookDistance

    // Used to know distances to each library
    val lastLocation: LiveData<Location> = LocationService.lastLocation

    init {
        performQueryAndSetBooks(daoQuery = ::allBooksWithLibName)
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

    fun performDatabaseQuery(daoQuery: () -> List<BookWithLibDetails>): List<BookWithLibDetails> {
        val bookList = daoQuery()
        return bookList
    }

    fun performQueryAndSetBooks(daoQuery: () -> List<BookWithLibDetails>) {
        viewModelScope.launch(Dispatchers.IO) {
            val bookList = performDatabaseQuery(daoQuery = daoQuery)
            withContext(Dispatchers.Main) {
                setDisplayedBooks(bookList)
            }
        }
    }

    fun setSelectedBook(book: BookWithLibDetails, distance: Float?) {
        _selectedBook.value = book
        _selectedBookDistance.value = distance
    }

    fun setDisplayedBooks(bookList: List<BookWithLibDetails>) {
        _books.value = bookList
    }

    // Expected to return null before first location has been obtained by LocationService
    // This is dealt with by the data binding expressions
    // Value formatted to 1dp by string resource
    fun distFromMyLocation(destLatitude: Double, destLongitude: Double): Float? {
        val endLocation = Location("end")
        endLocation.latitude = destLatitude
        endLocation.longitude = destLongitude

        val distInMetres = lastLocation.value?.distanceTo(endLocation)
        val distInKm = (distInMetres?.div(1000))

        return distInKm
    }

    // Null distance means no distance filter is applied
    fun filterBooksByDistance(distanceFilter: Int?, bookList: List<BookWithLibDetails>):
            List<BookWithLibDetails> {
        // Remove no books if no distance filter is applied
        if (distanceFilter == null) {
            return bookList
        }
        val filteredList: MutableList<BookWithLibDetails> = mutableListOf()
        for (book in bookList) {
            // If distance is null then no distance found - do not filter out
            val distance = distFromMyLocation(book.bookLibLatitude, book.bookLibLongitude)
            if (distance == null || distance <= distanceFilter) {
                filteredList.add(book)
            }
        }
        return filteredList
    }

    fun setLastQuery(query: String) {
        _lastQuery.value = query
    }

    fun setSubjectFilter(newSubjectFilter: String?) {
        // Check new subject is different from currently applied filter
        // If not different, do nothing
        // If different, change applied filter in ViewModel then update book list accordingly
        if (newSubjectFilter != _subjectFilter.value) {
            _subjectFilter.value = newSubjectFilter
            applyAllFilters(_lastQuery.value!!, subjectFilter.value, distanceFilter.value)
        }
    }

    fun setDistanceFilter(newDistanceFilter: Int?) {
        // Check new distance is different from currently applied filter
        // If not different, do nothing
        // If different, change applied filter in ViewModel then update book list accordingly
        if (newDistanceFilter != _distanceFilter.value) {
            _distanceFilter.value = newDistanceFilter
            applyAllFilters(_lastQuery.value!!, subjectFilter.value, distanceFilter.value)
        }
    }

    fun applyDatabaseFilters(searchQuery: String, subjectFilter: String?): Deferred<List<BookWithLibDetails>> {
        return viewModelScope.async(Dispatchers.IO) {
            val appropriateDaoQuery = generateCorrectDaoQuery(searchQuery, subjectFilter)
            val queryResult = performDatabaseQuery(daoQuery = appropriateDaoQuery)
            queryResult
        }
    }

    // Distance filter logic is more complicated than other filters as must check distance between
    // each book and the user so requires a separate method
    fun applyDistanceFilter(distanceFilter: Int?, bookList: List<BookWithLibDetails>): Deferred<List<BookWithLibDetails>> {
        return viewModelScope.async(Dispatchers.Default) {
            val filteredBookList = filterBooksByDistance(distanceFilter, bookList)
            filteredBookList
        }
    }

    fun applyAllFilters(searchQuery: String, subjectFilter: String?, distanceFilter: Int?) {
        // Send new database query using provided search query and subject filters
        val queryResult: Deferred<List<BookWithLibDetails>> =
            applyDatabaseFilters(searchQuery, subjectFilter)
        // With new list of books, filter out any based on distance to the user
        viewModelScope.launch(Dispatchers.Default) {
            val listToBeFilteredByDistance: Deferred<List<BookWithLibDetails>> =
                applyDistanceFilter(distanceFilter, queryResult.await())
            val filteredList = listToBeFilteredByDistance.await()
            withContext(Dispatchers.Main) {
                setDisplayedBooks(filteredList)
            }
        }
    }

//    fun setDistanceFilter(newDistanceFilter: Int?) {
//        // If no distance filter currently applied simply filter current list
//        if (_distanceFilter.value == null) {
//            viewModelScope.launch(Dispatchers.Default) {
//                val filteredBookList = filterBooksByDistance(newDistanceFilter, _books.value!!)
//                withContext(Dispatchers.Main) {
//                    setDisplayedBooks(filteredBookList)
//                }
//            }
//        }
//        // If a distance filter is currently applied perform database query again to retrieve
//        // books which may have been filtered out
//        else {
//            // Cannot use performQueryAndSetBooks method as we should switch to Default thread
//            // for distance calculations
//            viewModelScope.launch(Dispatchers.IO) {
//                val appropriateDaoQuery = generateCorrectDaoQuery(_lastQuery.value!!, subjectFilter.value)
//                val booksList = performDatabaseQuery(daoQuery = appropriateDaoQuery)
//                withContext(Dispatchers.Default) {
//                    val filteredBookList = filterBooksByDistance(newDistanceFilter, booksList)
//                    withContext(Dispatchers.Main) {
//                        setDisplayedBooks(filteredBookList)
//                    }
//                }
//            }
//        }
//        // Finally, store the distance filter which was applied
//        _distanceFilter.value = newDistanceFilter
//    }

//    fun setSubjectFilter(subject: String?) {
//        _subjectFilter.value = subject
//        val appropriateDaoQuery =
//            generateCorrectDaoQuery(_lastQuery.value!!, subjectFilter.value)
//        performQueryAndSetBooks(daoQuery = appropriateDaoQuery)
//    }
//
//    enum class Filter { SUBJECT, DISTANCE }
}