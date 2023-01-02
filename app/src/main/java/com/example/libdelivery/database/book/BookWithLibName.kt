package com.example.libdelivery.database.book;

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookWithLibName (
    @PrimaryKey
    val id: Int,
    @NonNull @ColumnInfo(name = "title") val bookTitle: String,
    @NonNull @ColumnInfo(name = "image_url") val bookImgUrl: String,
    @NonNull @ColumnInfo(name = "authors") val bookAuthors: String,
    @NonNull @ColumnInfo(name = "subject") val bookSubject: String,
    @NonNull @ColumnInfo(name = "year_published") val bookYearPub: Int,
    @NonNull @ColumnInfo(name = "copies_available") val bookCopies: Int,
    @NonNull @ColumnInfo(name = "library_name") val bookLibName: String
)