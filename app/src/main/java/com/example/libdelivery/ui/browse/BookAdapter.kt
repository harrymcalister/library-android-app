package com.example.libdelivery.ui.browse

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libdelivery.database.book.Book
import com.example.libdelivery.databinding.BookItemBinding
import java.sql.Date
import java.text.SimpleDateFormat

class BookAdapter : ListAdapter<Book,
        BookAdapter.BookViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }
        }
    }

    class BookViewHolder(private var binding: BookItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.book = book
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookAdapter.BookViewHolder {
        return BookViewHolder(BookItemBinding.inflate(
            LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: BookAdapter.BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book)
    }
}