package com.example.librarybrowse.database.book

import androidx.room.Dao
import androidx.room.Query

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): List<Book>

    @Query("SELECT b.id, b.title, b.image_url, b.authors, b.subject, b.year_published, b.copies_available, b.description, l.library_name, l.latitude, l.longitude FROM books b LEFT JOIN libraries l ON b.library_id = l.id ORDER BY title ASC")
    fun getAllBooksWithLibDetails(): List<BookWithLibDetails>

    @Query("SELECT * FROM (SELECT b.id, b.title, b.image_url, b.authors, b.subject, b.year_published, b.copies_available, b.description, l.library_name, l.latitude, l.longitude FROM books b LEFT JOIN libraries l ON b.library_id = l.id) WHERE title || '{}' || authors LIKE '%' || :searchQuery || '%' ORDER BY title ASC")
    fun searchBooksWithLibDetails(searchQuery: String): List<BookWithLibDetails>

    @Query("SELECT * FROM (SELECT b.id, b.title, b.image_url, b.authors, b.subject, b.year_published, b.copies_available, b.description, l.library_name, l.latitude, l.longitude FROM books b LEFT JOIN libraries l ON b.library_id = l.id) WHERE subject = :subject ORDER BY title ASC")
    fun getAllBooksWithSubject(subject: String): List<BookWithLibDetails>

    @Query("SELECT * FROM (SELECT b.id, b.title, b.image_url, b.authors, b.subject, b.year_published, b.copies_available, b.description, l.library_name, l.latitude, l.longitude FROM books b LEFT JOIN libraries l ON b.library_id = l.id) WHERE subject = :subject AND title || '{}' || authors LIKE '%' || :searchQuery || '%' ORDER BY title ASC")
    fun searchBooksWithSubject(searchQuery: String, subject: String): List<BookWithLibDetails>

    @Query("SELECT * FROM books WHERE title = :bookTitle ORDER BY title ASC")
    fun getByBookTitle(bookTitle: String): List<Book>
}