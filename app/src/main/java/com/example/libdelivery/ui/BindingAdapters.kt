package com.example.libdelivery.ui

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.libdelivery.R
import com.example.libdelivery.database.book.Book
import com.example.libdelivery.ui.browse.BookAdapter
import com.example.libdelivery.ui.browse.LibraryApiStatus

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}
/*
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView,
                     data: List<Book>?) {
    val adapter = recyclerView.adapter as BookAdapter
    adapter.submitList(data)
}
*/

// Do not currently need this method as a local database is used
@BindingAdapter("libraryApiStatus")
fun bindStatus(statusImageView: ImageView,
               status: LibraryApiStatus?) {
    when (status) {
        LibraryApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        LibraryApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
        else -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
    }
}