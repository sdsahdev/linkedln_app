package com.example.employe_manage.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.employe_manage.Adapter.UserAdapter
import com.example.employe_manage.Modals.AuthManager
import com.example.employe_manage.Modals.User
import com.example.employe_manage.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var usersAdapter: UserAdapter
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar
    private lateinit var nodata: LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference.child("users")
        progressBar = view.findViewById(com.example.employe_manage.R.id.progressBar)

        usersRecyclerView = view.findViewById(R.id.usersRecyclerView)
        usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        usersAdapter = UserAdapter()
        usersRecyclerView.adapter = usersAdapter
        searchView = view.findViewById(R.id.searchView)
        nodata = view.findViewById(R.id.nodata)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                usersAdapter.filter(newText.orEmpty())
                return true
            }
        })

        loadUsers()
    }

        private fun loadUsers() {
            progressBar?.visibility = View.VISIBLE // Show ProgressBar

            val usersListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val usersList = mutableListOf<User>()
                    val currentUserId = AuthManager.getCurrentUserId() // Assuming you have a method to get the current user's ID

                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            // Add the user to the list only if it's not the current user
                            if (it.userId != currentUserId) {
                                usersList.add(it)
                            }
                        }
                    }
                    if(usersList.size == 0){
                        nodata.visibility =  View.VISIBLE
                    }else{

                        nodata.visibility = View.GONE
                    }
                    usersAdapter.submitList(usersList)
                    progressBar?.visibility = View.GONE
                }


                override fun onCancelled(databaseError: DatabaseError) {
                    progressBar?.visibility = View.GONE
                    Log.w("CandidateActivity", "loadUsers:onCancelled", databaseError.toException())
                }
            }
            database.addValueEventListener(usersListener)
        }

}