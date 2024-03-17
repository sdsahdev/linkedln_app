package com.example.employe_manage.Modals

import com.google.firebase.database.FirebaseDatabase

data class Post(
    val postId: String = "",
    val userId: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0,
    val username: String = "",
    val userProfile: String = "",
)

