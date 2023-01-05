package com.example.libdelivery.ui.browse

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.libdelivery.LibDeliveryApplication
import com.example.libdelivery.database.book.Book
import com.example.libdelivery.database.book.BookWithLibDetails
import com.example.libdelivery.databinding.BookItemBinding
import com.example.libdelivery.ui.viewmodel.SharedViewModel
import com.example.libdelivery.ui.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.*
import java.sql.Date
import java.text.SimpleDateFormat
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
        Observer<String>, CoroutineScope {

        // Construct coroutine scope
        // Use Default dispatcher to avoid slowing down the UI thread for distance calcs
        private val job = Job()
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.Default + job

        // Make result an empty string by default so data binding doesn't break
        private val distanceCalcsResult = MutableLiveData<String>("")

        // This holder will observe the MutableLiveData field to allow UI updates
        init {
            distanceCalcsResult.observeForever(this)
        }

        // User location stored in viewModel instance
        fun calculateDistance(book: BookWithLibDetails, viewModel: SharedViewModel) {
            launch {
                val distString = viewModel.formattedDistFromMyLocation(book.bookLibLatitude, book.bookLibLongitude)
                // Prevents the result being updated if we have scrolled away/job is cancelled
                if (isActive) {
                    // Need to be on the Main thread to set LiveData value
                    withContext(Dispatchers.Main) {
                        distanceCalcsResult.value = distString
                    }
                }
            }
        }

        override fun onChanged(distString: String) {
            binding.distanceString = distString
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

class BookListener(val clickListener: (book: BookWithLibDetails) -> Unit) {
    fun onClick(book: BookWithLibDetails) = clickListener(book)
}