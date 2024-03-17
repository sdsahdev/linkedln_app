package com.example.employe_manage.Modals

    data class Other_user(
        val userId: String = "",
        val username: String = "",
        val email: String = "",
        val company: String = "",
        val profession: String = "",
        val phoneNumber: String = "",
        val collage: String = "",
        val school: String = "",
        val city: String = "",
        val county: String = "",
        val image: String = "",
        val connections: MutableList<String> = mutableListOf()  // List to store user IDs of connections
    )