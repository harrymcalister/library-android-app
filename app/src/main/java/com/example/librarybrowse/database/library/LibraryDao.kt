package com.example.librarybrowse.database.library

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LibraryDao {
    @Query("SELECT * FROM libraries ORDER BY library_name")
    fun getAllLibraries(): List<Library>

    @Query("SELECT * FROM libraries WHERE library_name = :libName ORDER BY library_name")
    fun getByLibraryName(libName: String): List<Library>

    @Query("SELECT * FROM libraries WHERE id = :libId")
    fun getByLibraryId(libId: Int): List<Library>
}