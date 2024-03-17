package com.example.employe_manage.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.employe_manage.Modals.Post
import com.example.employe_manage.R

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val postsList = mutableListOf<Post>()

    fun submitList(newList: List<Post>) {
        postsList.clear()
        postsList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postsList[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = postsList.size

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postContentTextView: TextView = itemView.findViewById(R.id.postContentTextView)
        private val username: TextView = itemView.findViewById(R.id.username)
        private val postImageView: ImageView = itemView.findViewById(R.id.postImageView) // Assuming ImageView ID is postImageView
        private val profile: ImageView = itemView.findViewById(R.id.profile) // Assuming ImageView ID is postImageView

        // Add more views if needed

        fun bind(post: Post) {
            postContentTextView.text = post.description
            username.text = post.username
            if(!post.imageUrl.isEmpty()) {
                Glide.with(itemView.context)
                    .load(post.imageUrl)
                    .placeholder(R.drawable.image) // Placeholder image while loading
                    .error(R.drawable.image) // Error image if loading fails
                    .into(postImageView)
            }else{
                postImageView.isVisible = false
            }
            Glide.with(itemView.context)
                .load(post.userProfile)
                .placeholder(R.drawable.user) // Placeholder image while loading
                .error(R.drawable.user) // Error image if loading fails
                .into(profile)
            // Bind other views here
        }

    }
}
