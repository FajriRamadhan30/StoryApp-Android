package com.project.storyapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.storyapp.R

class LoadingStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading_state, parent, false)
        return LoadingStateViewHolder(view, retry)
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingStateViewHolder(view: View, retry: () -> Unit) : RecyclerView.ViewHolder(view) {
        private val progressBar: View = itemView.findViewById(R.id.progress_bar)
        private val errorText: View = itemView.findViewById(R.id.error_text)
        private val retryButton: View = itemView.findViewById(R.id.retry_button)

        init {
            retryButton.setOnClickListener {
                retry.invoke()  // Retry action
            }
        }

        fun bind(loadState: LoadState) {
            when (loadState) {
                is LoadState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    errorText.visibility = View.GONE
                    retryButton.visibility = View.GONE
                }
                is LoadState.Error -> {
                    progressBar.visibility = View.GONE
                    errorText.visibility = View.GONE
                    retryButton.visibility = View.GONE
                }
                is LoadState.NotLoading -> {
                    // Hide loading indicator and retry button when data is fully loaded or no more data
                    progressBar.visibility = View.GONE
                    errorText.visibility = View.GONE
                    retryButton.visibility = View.GONE
                }
            }
        }
    }
}
