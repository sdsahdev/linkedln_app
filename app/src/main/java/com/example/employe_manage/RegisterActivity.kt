package com.example.employe_manage

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.employe_manage.Modals.User
import com.example.employe_manage.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private var selectedImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        val signUpButton: Button = findViewById(R.id.signUpButton)
        binding.selectImageButton.setOnClickListener {
            selectImage()
        }
        signUpButton.setOnClickListener { signUp() }
    }
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // Upload image to Firebase Storage
                selectedImageUri = uri
                binding.postImageView.setImageURI(uri)
            }
        }
    }

    private fun uploadImage(imageUri: Uri, UserId : String) {

        val username = binding.userTc.text.toString().trim()
        val company = binding.companyTx.text.toString().trim()
        val profession = binding.professionTx.text.toString().trim()
        val email = binding.emailTx.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val phoneNumber = binding.phonenumberTx.text.toString().trim()
        val collage = binding.collageTx.text.toString().trim()
        val school = binding.schoolTx.text.toString().trim()
        val city = binding.cityTx.text.toString().trim()
        val county = binding.countyTx.text.toString().trim()
        val imageName = "profile_${UUID.randomUUID()}.jpg" // Generate a unique image name
        val storageReference = FirebaseStorage.getInstance().reference.child("images/$imageName")

        val uploadTask = storageReference.putFile(imageUri)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Image uploaded successfully
                // Get the download URL and save it to the database
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Log.d("SignupActivity", "Image uploaded successfully: $imageUrl")

                    // Save imageUrl along with other user data in the Realtime Database
                    saveUserData(UserId , username, company, profession, email, phoneNumber, collage, school, city, county, imageUrl,password)
                }.addOnFailureListener { e ->
                    binding.progressBar?.visibility = View.GONE

                    // Failed to get download URL
                    Log.e("SignupActivity", "Error getting download URL: $e")
                }
            } else {
                // Image upload failed
                Log.e("SignupActivity", "Image upload failed: ${task.exception}")
                Toast.makeText(this, "Failed to upload image. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        val pattern = Pattern.compile(emailRegex)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        val phoneRegex = "^[0-9]{10}\$"
        val pattern = Pattern.compile(phoneRegex)
        val matcher = pattern.matcher(phoneNumber)
        return matcher.matches()
    }

    private fun signUp() {
        val username = binding.userTc.text.toString().trim()
        val company = binding.companyTx.text.toString().trim()
        val profession = binding.professionTx.text.toString().trim()
        val email = binding.emailTx.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val phoneNumber = binding.phonenumberTx.text.toString().trim()
        val collage = binding.collageTx.text.toString().trim()
        val school = binding.schoolTx.text.toString().trim()
        val city = binding.cityTx.text.toString().trim()
        val county = binding.countyTx.text.toString().trim()


        if (!isEmailValid(email)) {

            Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show()
            return
        }else  if (!isPasswordValid(password)) {
            Toast.makeText(this, "Password at least 8 characters", Toast.LENGTH_SHORT).show()
            return
        }else if (!isPhoneNumberValid(phoneNumber)) {
            Toast.makeText(this, "Phone number is not valid", Toast.LENGTH_SHORT).show()
            return
        }else if(selectedImageUri == null || selectedImageUri.toString().isEmpty() ){
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show()
            return
        }else{

            if (username.isEmpty() || company.isEmpty() || profession.isEmpty() || email.isEmpty() ||
                phoneNumber.isEmpty() || collage.isEmpty() || school.isEmpty() || city.isEmpty() || county.isEmpty() || selectedImageUri.toString().isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return
            }
            binding.progressBar?.visibility = View.VISIBLE // Show ProgressBar

            // Perform user registration
            auth.createUserWithEmailAndPassword(
                email,
                password
            ) // Note: Password should be minimum 6 characters
                .addOnCompleteListener(this) { task ->


                    if (task.isSuccessful) {
                        // Sign up success
                        val user = auth.currentUser
                        val userId = user?.uid ?: ""

                        // Upload user image
                        selectedImageUri?.let { uri ->
                            uploadImage(uri, userId)
                        }

                        // Navigate to the main activity
                        // Finish this activity to prevent going back to it on pressing back button
                    } else {
                        binding.progressBar?.visibility = View.GONE

                        // Sign up failed
                        Log.w("SignupActivity", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this,
                            "Sign up failed. Please try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }
    fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    private fun saveUserData(

        userId: String,
        username: String,
        company: String,
        profession: String,
        email: String,
        phoneNumber: String,
        collage: String,
        school: String,
        city: String,
        county: String,
        image: String,
        password: String
    ) {
        val database = FirebaseDatabase.getInstance().reference
        val userData = mapOf(
            "userId" to userId,
            "username" to username,
            "company" to company,
            "profession" to profession,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "collage" to collage,
            "school" to school,
            "city" to city,
            "county" to county,
            "image" to image,
            "password" to password
            // Add other user information fields to the map
        )
        database.child("users").child(userId).setValue(userData)
            .addOnSuccessListener {
                binding.progressBar?.visibility = View.GONE

                Toast.makeText(this, "User Register", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Bottomtabas::class.java))
                finish()
                val user = auth.currentUser
                user?.let {
                    val userId = user.uid
                    getUserData(userId)
                }
                // User data successfully stored in the database
            }
            .addOnFailureListener { e ->
                binding.progressBar?.visibility = View.GONE

                // Failed to store user data in the database
                Log.e("SignupActivity", "Error storing user data: $e")
            }
    }

    private fun getUserData(userId: String) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.getValue(User::class.java)
                userData?.let {
                    // Save user data to shared preference or singleton class
                    UserManagementActivity.CurrentUser.userData = it
                    // Redirect to the main activity or any other activity
                    startActivity(Intent(this@RegisterActivity, Bottomtabas::class.java))
                    finish()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("error", "onCancelled: "+databaseError)
                // Handle database error
            }
        })
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 123
    }
}