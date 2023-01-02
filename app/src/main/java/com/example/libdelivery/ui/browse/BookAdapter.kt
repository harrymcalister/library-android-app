package com.example.libdelivery.ui.browse

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libdelivery.database.book.Book
import com.example.libdelivery.database.book.BookWithLibName
import com.example.libdelivery.databinding.BookItemBinding
import java.sql.Date
import java.text.SimpleDateFormat

class BookAdapter(val clickListener: BookListener) : ListAdapter<BookWithLibName,
        BookAdapter.BookViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<BookWithLibName>() {
            override fun areItemsTheSame(oldItem: BookWithLibName, newItem: BookWithLibName): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BookWithLibName, newItem: BookWithLibName): Boolean {
                return oldItem == newItem
            }
        }
    }

    class BookViewHolder(private var binding: BookItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: BookListener, bookWithLibName: BookWithLibName) {
            binding.bookWithLibName = bookWithLibName
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val viewHolder = BookViewHolder(BookItemBinding.inflate(
            LayoutInflater.from(parent.context)))
        return viewHolder
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(clickListener, book)
    }
}

class BookListener(val clickListener: (book: BookWithLibName) -> Unit) {
    fun onClick(book: BookWithLibName) = clickListener(book)
}