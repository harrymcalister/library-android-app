package com.example.libdelivery.database.book

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.libdelivery.database.library.Library

// Link library_id to id of 'libraries' table with a foreign key
@Entity(tableName = "books",
    foreignKeys = [ForeignKey(
        entity = Library::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("library_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Book (
    @PrimaryKey val id: Int,
    @NonNull @ColumnInfo(name = "title") val bookTitle: String,
    @NonNull @ColumnInfo(name = "image_url") val bookImgUrl: String,
    @NonNull @ColumnInfo(name = "authors") val bookAuthors: String,
    @NonNull @ColumnInfo(name = "subject") val bookSubject: String,
    @NonNull @ColumnInfo(name = "year_published") val bookYearPub: Int,
    @NonNull @ColumnInfo(name = "copies_available") val bookCopies: Int,
    @NonNull @ColumnInfo(name = "library_id") val bookLibId: Int,

)