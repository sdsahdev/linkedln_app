package com.example.employe_manage.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.example.employe_manage.Modals.AuthManager
import com.example.employe_manage.Modals.CurrentUserManager
import com.example.employe_manage.Modals.Post
import com.example.employe_manage.Modals.User
import com.example.employe_manage.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class UploadFragment : Fragment() {
    private lateinit var postContentEditText: EditText
    private lateinit var selectImageButton: Button
    private var selectedImageUri: Uri? = null
    private lateinit var postImageView: ImageView
    private lateinit var sendDataButton: Button
    private lateinit var currentUser: User
    private lateinit var progressBar: ProgressBar
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference.child("posts")
        sendDataButton = view.findViewById(R.id.sendDataButton)
        postContentEditText = view.findViewById(R.id.postContentEditText)
        postImageView = view.findViewById(R.id.postImageView)
        selectImageButton = view.findViewById(R.id.selectImageButton)
        progressBar = view.findViewById(R.id.progressBar)

        selectImageButton.setOnClickListener {
            selectImage()
        }
        sendDataButton.setOnClickListener {
            writeDataToFirebase()
        }
    }

    private fun writeDataToFirebase() {
        val currentUser = CurrentUserManager.getCurrentUser()
        val postId = database.push().key ?: ""
        val userId = AuthManager.getCurrentUserId() ?: ""
        val content = postContentEditText.text.toString().trim()
        val timestamp = System.currentTimeMillis()
        val username = currentUser?.username
        val userProfile = currentUser?.image
        progressBar.visibility = View.VISIBLE // Show ProgressBar
        if (selectedImageUri != null) {
            val imageRef =
                FirebaseStorage.getInstance().reference.child("images").child("$postId.jpg")
            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val post = Post(
                            postId,
                            userId,
                            content,
                            imageUrl,
                            timestamp,
                            username.toString(),
                            userProfile.toString()
                        )
                        val postsRef = database.child(postId)
                        postsRef.setValue(post)
                            .addOnSuccessListener {
                                progressBar?.visibility = View.GONE

                                // Post successfully written to the database
                                // Clear the EditText after posting
                                postContentEditText.setText("")
                                Toast.makeText(
                                    context,
                                    "Image Upload Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Clear the ImageView
                                postImageView.setImageResource(R.drawable.image)
                                // Reset selectedImageUri
                                selectedImageUri = null
                            }
                            .addOnFailureListener { e ->
                                progressBar?.visibility = View.GONE

                                Toast.makeText(context, "error " + e, Toast.LENGTH_SHORT).show()
                                // Failed to write post to the database
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Failed to upload image to Firebase Storage
                }
        } else {
            // No image selected
            // Write post without image
            if(!content.isEmpty()) {


                val post = Post(
                    postId,
                    userId,
                    content,
                    "",
                    timestamp,
                    username.toString(),
                    userProfile.toString()
                )
                val postsRef = database.child(postId)
                postsRef.setValue(post)
                    .addOnSuccessListener {
                        // Post successfully written to the database
                        // Clear the EditText after posting
                        postContentEditText.setText("")
                        progressBar?.visibility = View.GONE

                    }
                    .addOnFailureListener { e ->
                        progressBar?.visibility = View.GONE

                        // Failed to write post to the database
                    }
            }else{
                Toast.makeText(context, "Please select image or write description", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                postImageView.setImageURI(uri)
            }
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    companion object {
        const val REQUEST_IMAGE_PICK = 123
    }

}