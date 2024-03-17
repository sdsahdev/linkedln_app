package com.example.employe_manage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.employe_manage.Adapter.PostAdapter
import com.example.employe_manage.Modals.AuthManager
import com.example.employe_manage.Modals.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


// MainActivity.kt
class MainActivity : AppCompatActivity() {

//    private lateinit var database: DatabaseReference
//    private lateinit var postsRecyclerView: RecyclerView
//    private lateinit var postsAdapter: PostAdapter
//    private lateinit var postImageView: ImageView
//    private lateinit var sendDataButton: Button
//    private lateinit var navigatecandi: Button
//    private lateinit var logout: Button
//
//    private lateinit var postContentEditText: EditText
//    private lateinit var selectImageButton: Button
//    private var selectedImageUri: Uri? = null
//    val currentUser = UserManagementActivity.CurrentUser.userData

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        database = FirebaseDatabase.getInstance().reference.child("posts")
//        sendDataButton = findViewById(R.id.sendDataButton)
//        postsRecyclerView = findViewById(R.id.postsRecyclerView)
//        postsRecyclerView.layoutManager = LinearLayoutManager(this)
//        postsAdapter = PostAdapter()
//        postsRecyclerView.adapter = postsAdapter
//
//        navigatecandi = findViewById(R.id.navigatecandi)
//        logout = findViewById(R.id.logout)
//        postContentEditText = findViewById(R.id.postContentEditText)
//        postImageView = findViewById(R.id.postImageView)
//        selectImageButton = findViewById(R.id.selectImageButton)
//
//        loadPostsFromFollowedUsers()
//
////        selectImageButton.setOnClickListener {
////            selectImage()
////        }
//        logout.setOnClickListener {
//            signOut()
//        }
//
////        sendDataButton.setOnClickListener {
////            writeDataToFirebase()
////        }
//        navigatecandi.setOnClickListener {
////            val intent = Intent(this, CandidateActivity::class.java)
////            startActivity(intent)
//        }


    }


//    private fun loadPostsFromFollowedUsers() {
//        val rootRef = FirebaseDatabase.getInstance().reference
//        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//        Log.d("check_dev currentUserUid =>", "onDataChange: "+currentUserUid)
//        val listIdRef = rootRef.child("users").child(currentUserUid).child("connections")
//
//        val valueEventListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val followedUsers = dataSnapshot.children.mapNotNull { it.getValue(String::class.java) }
//                val postsListener = object : ValueEventListener {
//                    override fun onDataChange(postsSnapshot: DataSnapshot) {
//                        val postsList = mutableListOf<Post>()
//                        Log.d("check_dev post list", "onDataChange: "+postsList)
//                        for (postSnapshot in postsSnapshot.children) {
//
//                            val post = postSnapshot.getValue(Post::class.java)
//                            Log.d("check_dev post for", "onDataChange: "+post)
//
//                            post?.let {
//                                if (followedUsers.contains(it.userId)) {
//                                    postsList.add(it)
//                                }else if(currentUserUid.equals(it.userId)){
//                                    postsList.add(it)
//                                }else{
//
//                                }
//                            }
//                        }
//                        // Update UI with the posts of followed users
//                        postsAdapter.submitList(postsList)
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        Log.e("MainActivity", "loadPostsFromFollowedUsers:onCancelled", databaseError.toException())
//                    }
//                }
//                rootRef.child("posts").addListenerForSingleValueEvent(postsListener)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.e("MainActivity", "loadPostsFromFollowedUsers:onCancelled", databaseError.toException())
//            }
//        }
//        listIdRef.addListenerForSingleValueEvent(valueEventListener)
//    }
//
//    private fun signOut() {
//        FirebaseAuth.getInstance().signOut()
//        // Navigate back to login activity or splash activity
//        startActivity(Intent(this, UserManagementActivity::class.java))
//        finish() // Finish this activity to prevent going back to it on pressing back button
//    }
//    private fun selectImage() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//        startActivityForResult(intent, REQUEST_IMAGE_PICK)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
//            data?.data?.let { uri ->
//                selectedImageUri = uri
//                postImageView.setImageURI(uri)
//            }
//        }
//    }
//
//    private fun writeDataToFirebase() {
//        val postId = database.push().key ?: ""
//        val userId = AuthManager.getCurrentUserId() ?: ""
//        val content = postContentEditText.text.toString().trim()
//        val timestamp = System.currentTimeMillis()
//
//        if (selectedImageUri != null) {
//            val imageRef = FirebaseStorage.getInstance().reference.child("images").child("$postId.jpg")
//            imageRef.putFile(selectedImageUri!!)
//                .addOnSuccessListener { taskSnapshot ->
//                    imageRef.downloadUrl.addOnSuccessListener { uri ->
//                        val imageUrl = uri.toString()
//                        val post = Post(postId, userId, content, imageUrl, timestamp)
//                        val postsRef = database.child(postId)
//                        postsRef.setValue(post)
//                            .addOnSuccessListener {
//                                // Post successfully written to the database
//                                // Clear the EditText after posting
//                                postContentEditText.setText("")
//                                // Clear the ImageView
//                                postImageView.setImageResource(R.drawable.ic_launcher_background)
//                                // Reset selectedImageUri
//                                selectedImageUri = null
//                            }
//                            .addOnFailureListener { e ->
//                                Toast.makeText(this, "error "+e, Toast.LENGTH_SHORT).show()
//                                // Failed to write post to the database
//                            }
//                    }
//                }
//                .addOnFailureListener { e ->
//                    // Failed to upload image to Firebase Storage
//                }
//        } else {
//            // No image selected
//            // Write post without image
//            val post = Post(postId, userId, content, "", timestamp)
//            val postsRef = database.child(postId)
//            postsRef.setValue(post)
//                .addOnSuccessListener {
//                    // Post successfully written to the database
//                    // Clear the EditText after posting
//                    postContentEditText.setText("")
//                }
//                .addOnFailureListener { e ->
//                    // Failed to write post to the database
//                }
//        }
//    }
//
//
//
//    companion object {
//        const val REQUEST_IMAGE_PICK = 123
//    }
////    override fun onResume() {
////        super.onResume()
////        loadPostsFromFollowedUsers()
////        Toast.makeText(this, "resum", Toast.LENGTH_SHORT).show()
////    }
}
