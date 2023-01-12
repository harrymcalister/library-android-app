package com.example.librarybrowse.ui.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.librarybrowse.database.book.BookWithLibDetails
import com.example.librarybrowse.databinding.BookItemBinding
import com.example.librarybrowse.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BookAdapter(val sharedViewModel: SharedViewModel, val clickListener: BookListener) : ListAdapter<BookWithLibDetails,
        BookAdapter.BookViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<BookWithLibDetails>() {
            override fun areItemsTheSame(oldItem: BookWithLibDetails, newItem: BookWithLibDetails): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BookWithLibDetails, newItem: BookWithLibDetails): Boolean {
                return oldItem == newItem
            }
        }
    }

    class BookViewHolder(private var binding: BookItemBinding): RecyclerView.ViewHolder(binding.root),
        Observer<Float?>, CoroutineScope {

        // Construct coroutine scope
        // Use Default dispatcher to avoid slowing down the UI thread for distance calcs
        private val job = Job()
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.Default + job

        // Null values dealt with by data binding expression
        private val distanceCalcsResult = MutableLiveData<Float?>()

        // This holder will observe the MutableLiveData field to allow UI updates
        init {
            distanceCalcsResult.observeForever(this)
        }

        // User location stored in viewModel instance
        fun calculateDistance(book: BookWithLibDetails, viewModel: SharedViewModel) {
            launch {
                val distance = viewModel.distFromMyLocation(book.bookLibLatitude, book.bookLibLongitude)
                // Prevents the result being updated if we have scrolled away/job is cancelled
                if (isActive) {
                    // Need to be on the Main thread to set LiveData value
                    withContext(Dispatchers.Main) {
                        distanceCalcsResult.value = distance
                    }
                }
            }
        }

        override fun onChanged(distance: Float?) {
            binding.libDistance = distance
        }

        fun bind(viewModel: SharedViewModel, clickListener: BookListener, book: BookWithLibDetails) {
            binding.viewModel = viewModel
            binding.book = book
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookAdapter.BookViewHolder {
        val viewHolder = BookAdapter.BookViewHolder(
            BookItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
        return viewHolder
    }

    override fun onBindViewHolder(holder: BookAdapter.BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(sharedViewModel, clickListener, book)
        holder.calculateDistance(book, sharedViewModel)
    }
}

class BookListener(val clickListener: (book: BookWithLibDetails, distance: Float?) -> Unit) {
    fun onClick(book: BookWithLibDetails, distance: Float?) = clickListener(book, distance)
}