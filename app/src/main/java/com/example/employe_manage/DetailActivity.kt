package com.example.employe_manage

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.employe_manage.Modals.Other_user
import com.example.employe_manage.Modals.User
import com.example.employe_manage.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var database: DatabaseReference
    private lateinit var other_uers: Other_user
    private lateinit var currentUser: User

    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        userId = intent.getStringExtra("user_id").toString()


        binding.connectButton.setOnClickListener {
            toggleConnection()
        }

        loadOtheruser()
        loadCurrentUser()
    }

    private fun loadOtheruser() {
        binding.progressBar?.visibility = View.VISIBLE // Show ProgressBar

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        database = FirebaseDatabase.getInstance().reference.child("users")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar?.visibility = View.GONE

                // Check if the user exists
                if (snapshot.exists()) {
                    // Retrieve user data
                    val userData = snapshot.getValue(Other_user::class.java)
                    // Check if user data is not null
                    userData?.let {
                        // Assign the retrieved user data to other_uers
                        other_uers = it
                        // Now you can access other_uers's properties safely
                        val name = other_uers.username

                        binding.username.setText(other_uers.username)
                        binding.proffestion.setText(other_uers.profession)
                        binding.school.setText(other_uers.school)
                        binding.collage.setText(other_uers.collage)
                        binding.city.setText(other_uers.city)
                        binding.country.setText(other_uers.county)
                        binding.email.setText(other_uers.email)
                        binding.phonenumer.setText(other_uers.phoneNumber)
                        Glide.with(this@DetailActivity)
                            .load(other_uers.image)
                            .placeholder(R.drawable.image)
                            .error(R.drawable.image)
                            .into(binding.profilepic)


                        // Update UI or perform other actions here
                    }
                } else {
                    // User does not exist
                    Toast.makeText(this@DetailActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar?.visibility = View.GONE

                // Handle error
                Log.e("DetailActivity", "Error loading user data: ${error.message}")
                Toast.makeText(this@DetailActivity, "Error loading user data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
//                updateUI()
    }
    private fun loadCurrentUser() {
        binding.progressBar?.visibility = View.VISIBLE // Show ProgressBar

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar?.visibility = View.GONE

                // Check if the user exists
                if (snapshot.exists()) {
                    // Retrieve user data
                    val userData = snapshot.getValue(User::class.java)
                    // Check if user data is not null
                    userData?.let {
                        // Assign the retrieved user data to currentUser
                        currentUser = it
                        updateUI()
                        // Now you can access currentUser's properties safely
                        val name = currentUser.username

                        // Update UI or perform other actions here
                    }
                } else {
                    // User does not exist
                    Toast.makeText(this@DetailActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar?.visibility = View.GONE

                // Handle error
                Log.e("DetailActivity", "Error loading user data: ${error.message}")
                Toast.makeText(this@DetailActivity, "Error loading user data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val isAlreadyConnected = currentUser.connections.contains(userId)
        currentUser.connections.forEach { connectionId ->
        Log.d("check_idss  =>", connectionId)
    }
    Log.d("check_idss =>", "updateUI: "+userId)
    Log.d("check_idss =>", "updateUI: "+isAlreadyConnected)
        if (isAlreadyConnected) {
            // User is already connected, show unfollow option
            binding.connectButton.text = "Remove"
        } else {
            // User is not connected, show follow option
            binding.connectButton.text = "Connect"
        }
    }

    private fun toggleConnection() {
        // Check if the user is already connected
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val isAlreadyConnected = currentUser.connections.contains(userId)
        Log.d("check_dev dd ou", "toggleConnection: "+currentUser.connections)
        Log.d("check_dev dd cu", "toggleConnection: "+userId)
        if (isAlreadyConnected) {
            // User is already connected, so unfollow
            currentUser.connections.remove(userId)
        } else {
            // User is not connected, so follow
            currentUser.connections.add(userId)
        }

        // Update the connections list in the database
        updateConnectionsInDatabase()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun updateConnectionsInDatabase() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            database.child(currentUserId).child("connections").setValue(currentUser.connections)
            .addOnSuccessListener {
                // Successfully updated connections list in the database
                updateUI()
            }
            .addOnFailureListener { error ->
                Log.e(this.toString(), "Failed to update connections in database: ${error.message}")
            }
    }


}