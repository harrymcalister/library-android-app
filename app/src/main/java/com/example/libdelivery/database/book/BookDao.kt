package com.example.libdelivery.database.book

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY title")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE title = :bookTitle ORDER BY title")
    fun getByBookTitle(bookTitle: String): Flow<List<Book>>
}