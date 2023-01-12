package com.example.librarybrowse.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.librarybrowse.database.book.Book
import com.example.librarybrowse.database.book.BookDao
import com.example.librarybrowse.database.library.Library
import com.example.librarybrowse.database.library.LibraryDao

@Database(entities = [Library::class, Book::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun libraryDao(): LibraryDao
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database")
                    .createFromAsset("database/libdata.db")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }


}