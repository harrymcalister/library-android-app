package com.example.libdelivery.database.library

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM libraries ORDER BY library_name")
    fun getAllLibraries(): Flow<List<Library>>

    @Query("SELECT * FROM libraries WHERE library_name = :libName ORDER BY library_name")
    fun getByLibraryName(libName: String): Flow<List<Library>>

    @Query("SELECT * FROM libraries WHERE id = :libId")
    fun getByLibraryId(libId: Int): Flow<List<Library>>
}