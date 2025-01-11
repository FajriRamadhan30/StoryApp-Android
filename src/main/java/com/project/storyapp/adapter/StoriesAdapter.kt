package com.project.storyapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.storyapp.R
import com.project.storyapp.models.StoryItem

class StoriesAdapter(
    private val onClick: (StoryItem) -> Unit       // Click listener untuk item
) : PagingDataAdapter<StoryItem, StoriesAdapter.StoryViewHolder>(StoryDiffCallback()) {

    // ViewHolder class
    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_item_name)            // Nama story
        val description: TextView = itemView.findViewById(R.id.tv_item_description) // Deskripsi story
        val photo: ImageView = itemView.findViewById(R.id.iv_item_photo)        // Foto story
    }

    // DiffUtil callback untuk membandingkan item
    class StoryDiffCallback : DiffUtil.ItemCallback<StoryItem>() {
        override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
            // Bandingkan item berdasarkan ID (pastikan ID unik untuk setiap story)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
            // Bandingkan isi konten item (jika perlu, bisa lebih mendetail)
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        // Ambil data story berdasarkan posisi dari PagingData
        val story = getItem(position) // getItem digunakan pada PagingDataAdapter

        if (story != null) {
            // Set nama story ke TextView
            holder.name.text = story.name
            // Set deskripsi story ke TextView
            holder.description.text = story.description
            // Load foto dengan Glide
            Glide.with(holder.itemView.context)
                .load(story.photoUrl)
                .into(holder.photo)

            // Tambahkan animasi slide-in untuk setiap item
            val animation = android.view.animation.TranslateAnimation(
                android.view.animation.Animation.RELATIVE_TO_PARENT, -1f,  // Mulai dari kiri
                android.view.animation.Animation.RELATIVE_TO_PARENT, 0f,   // Berhenti di posisi aslinya
                android.view.animation.Animation.RELATIVE_TO_PARENT, 0f,
                android.view.animation.Animation.RELATIVE_TO_PARENT, 0f
            )
            animation.duration = 500 // Durasi animasi 500ms
            holder.itemView.startAnimation(animation)

            // Set listener untuk item yang di-klik
            holder.itemView.setOnClickListener {
                story?.let { onClick(it) } // Jalankan callback dengan data story yang di-klik
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() // PagingDataAdapter sudah mengelola jumlah item
    }
}
