package com.example.employe_manage.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.employe_manage.Adapter.PostAdapter
import com.example.employe_manage.Modals.CurrentUserManager
import com.example.employe_manage.Modals.Post
import com.example.employe_manage.Modals.User
import com.example.employe_manage.R
import com.example.employe_manage.UserManagementActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var postsAdapter: PostAdapter

    private lateinit var logout: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var nodata: LinearLayout

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(com.example.employe_manage.R.layout.fragment_home, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            database = FirebaseDatabase.getInstance().reference.child("posts")
            postsRecyclerView = view.findViewById(com.example.employe_manage.R.id.postsRecyclerView)
            postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            postsAdapter = PostAdapter()
            postsRecyclerView.adapter = postsAdapter

            progressBar = view.findViewById(com.example.employe_manage.R.id.progressBar)
            nodata = view.findViewById(R.id.nodata)

            logout = view.findViewById(com.example.employe_manage.R.id.logout)
            logout.setOnClickListener {
                signOut()
            }

            loadPostsFromFollowedUsers()
            loadCurrentUser()

        }
    private fun loadCurrentUser() {
        progressBar?.visibility = View.VISIBLE // Show ProgressBar
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserUid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the user exists
                if (snapshot.exists()) {
                    // Retrieve user data
                    val userData = snapshot.getValue(User::class.java)
                    // Check if user data is not null
                    userData?.let {
                        // Assign the retrieved user data to currentUser
                        CurrentUserManager.setCurrentUser(it)
                    }
                } else {
                    // User does not exist
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                }
                progressBar?.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                progressBar?.visibility = View.GONE
                Log.e("DetailActivity", "Error loading user data: ${error.message}")
                Toast.makeText(context, "Error loading user data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        // Navigate back to login activity or splash activity
        val intent = Intent(requireContext(), UserManagementActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    private fun loadPostsFromFollowedUsers() {

        progressBar?.visibility = View.VISIBLE // Show ProgressBar

        val rootRef = FirebaseDatabase.getInstance().reference
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        Log.d("check_dev currentUserUid =>", "onDataChange: "+currentUserUid)
        val listIdRef = rootRef.child("users").child(currentUserUid).child("connections")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val followedUsers = dataSnapshot.children.mapNotNull { it.getValue(String::class.java) }
                val postsListener = object : ValueEventListener {
                    override fun onDataChange(postsSnapshot: DataSnapshot) {
                        val postsList = mutableListOf<Post>()
                        Log.d("check_dev post list", "onDataChange: "+postsList)
                        for (postSnapshot in postsSnapshot.children) {

                            val post = postSnapshot.getValue(Post::class.java)
                            Log.d("check_dev post for", "onDataChange: "+post)

                            post?.let {
                                if (followedUsers.contains(it.userId)) {
                                    postsList.add(it)
                                }else if(currentUserUid.equals(it.userId)){
                                    postsList.add(it)
                                }else{

                                }
                            }
                        }
                        // Update UI with the posts of followed users
                        postsAdapter.submitList(postsList)

                        if(postsList.size == 0){
                            nodata.visibility =  View
                                .VISIBLE
                        }else{

                            nodata.visibility = View.GONE
                        }
                        progressBar?.visibility = View.GONE
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        progressBar?.visibility = View.GONE
                        Log.e("MainActivity", "loadPostsFromFollowedUsers:onCancelled", databaseError.toException())
                    }
                }
                rootRef.child("posts").addListenerForSingleValueEvent(postsListener)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                progressBar?.visibility = View.GONE
                Log.e("MainActivity", "loadPostsFromFollowedUsers:onCancelled", databaseError.toException())
            }
        }
        listIdRef.addListenerForSingleValueEvent(valueEventListener)
    }


}