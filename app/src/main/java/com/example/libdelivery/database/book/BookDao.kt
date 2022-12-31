package com.example.libdelivery.database.book

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT b.id, b.title, b.image_url, b.authors, b.subject, b.year_published, b.copies_available, l.library_name FROM books b LEFT JOIN libraries l ON b.library_id = l.id")
    fun getAllBooksWithLibName(): Flow<List<BookWithLibName>>

    @Query("SELECT * FROM books WHERE title = :bookTitle ORDER BY title")
    fun getByBookTitle(bookTitle: String): Flow<List<Book>>
}