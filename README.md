# library-android-app

An Android app for browsing local library books.

Any feedback about the code or architecture is much appreciated!

This app was made in an effort to improve my ability to make an Android app using Views and XML Layouts from scratch. The goal was to make a
proof-of-concept for a library browsing app, which can collate the book collections of many local libraries to allow you to browse and filter them
simulataneously.

Currently the main page of the app ('Browse' on the Bottom Navbar) allows you to scroll through a list of books stored in a local database. The database
contains a table of participating libraries (currently placeholder example libraries) which are referenced by key in the books table. The books table is a
collection of all books available for browsing.

In a real implementation of the app this database would be hosted on a server and the data would be accessed via GET requests. Additionally there is room
to improve the efficiency and scalability as the app was designed with the idea of accessing the local database.

Each book in the list can be clicked to access a separate fragment providing further details about the book.

There is a search bar and filter menu, which either filter the results by using different SQL queries or by comparing each book's location against
the user's current location to find the distance and filter appropriately.

This allowed me to practice using the following features:
+ SQL/SQLite3
+ LiveData
+ Coroutines
+ Room
+ Data Binding,
+ RecyclerView
+ Coil
+ Jetpack Navigation
+ ViewModel
+ MenuProvider
+ Google Play LocationServices
