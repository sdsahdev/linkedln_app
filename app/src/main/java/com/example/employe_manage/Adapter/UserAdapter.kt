package com.example.employe_manage.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.employe_manage.DetailActivity
import com.example.employe_manage.Modals.User
import com.example.employe_manage.R
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val userList = mutableListOf<User>()
    private val filteredList = mutableListOf<User>()

    fun submitList(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        filteredList.clear()
        filteredList.addAll(newList)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_candidate, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = filteredList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = filteredList.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val navigateDetail: MaterialCardView = itemView.findViewById(R.id.navigateDetail)
        private val userTc: TextView = itemView.findViewById(R.id.username)
        private val profession: TextView = itemView.findViewById(R.id.professionTx)
        private val profile: ImageView = itemView.findViewById(R.id.profile)

        fun bind(user: User) {
            userTc.text = user.username
            profession.text = user.profession

            Glide.with(itemView.context)
                .load(user.image)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(profile)
        }

        init {
            navigateDetail.setOnClickListener {
                val user = filteredList[adapterPosition]
                val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                    putExtra("user_id", user.userId)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(userList)
        } else {
            val lowerCaseQuery = query.toLowerCase(Locale.getDefault())
            userList.forEach { user ->
                if (user.username.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredList.add(user)
                }
            }
        }
        notifyDataSetChanged()
    }
}
