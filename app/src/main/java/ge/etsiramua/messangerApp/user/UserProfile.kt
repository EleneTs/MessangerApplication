package ge.etsiramua.messangerApp.user

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseUser
import com.mikhaellopez.circularimageview.CircularImageView
import ge.etsiramua.messangerApp.main.MainActivity
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.model.User

class ProfileActivity : AppCompatActivity() {
    companion object {
        private const val GALLERY_REQUEST_CODE = 123
    }

    private lateinit var nicknameEditText: EditText
    private lateinit var careerEditText: EditText
    private lateinit var homeButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var settingsButton: ImageView
    private lateinit var profilePhotoImageView: CircularImageView
    private lateinit var updateButton: MaterialButton
    private lateinit var signOutButton: MaterialButton

    private var selectedImageUri: Uri? = null
    private var retrievedUser: User? = null
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, UserViewModelFactory(application)).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews()
        startProgressBar()

        val user = IntentCompat.getParcelableExtra(intent, "currentUser", FirebaseUser::class.java)
        setupListeners(user)
        displayUser(user)
    }

    private fun initViews() {
        nicknameEditText = findViewById(R.id.nickname)
        careerEditText = findViewById(R.id.career)
        homeButton = findViewById(R.id.home_button)
        settingsButton = findViewById(R.id.settings_button)
        profilePhotoImageView = findViewById(R.id.profile_photo)
        progressBar = findViewById(R.id.profileProgressBar)
        signOutButton = findViewById(R.id.sign_out_button)
        updateButton = findViewById(R.id.update_button)
    }

    private fun startProgressBar() {
        progressBar.visibility = View.VISIBLE
        nicknameEditText.visibility = View.INVISIBLE
        careerEditText.visibility = View.INVISIBLE
        homeButton.visibility = View.INVISIBLE
        settingsButton.visibility = View.INVISIBLE
        profilePhotoImageView.visibility = View.INVISIBLE
        signOutButton.visibility = View.INVISIBLE
        updateButton.visibility = View.INVISIBLE
    }

    private fun stopProgressBar() {
        progressBar.visibility = View.GONE
        nicknameEditText.visibility = View.VISIBLE
        careerEditText.visibility = View.VISIBLE
        homeButton.visibility = View.VISIBLE
        settingsButton.visibility = View.VISIBLE
        profilePhotoImageView.visibility = View.VISIBLE
        signOutButton.visibility = View.VISIBLE
        updateButton.visibility = View.VISIBLE
    }

    private fun setupListeners(user: FirebaseUser?) {
        homeButton.setOnClickListener {
            redirectToHomepage(user)
        }

        profilePhotoImageView.setOnClickListener {
            openGalleryForImage()
        }

        signOutButton.setOnClickListener {
            signOut()
        }

        updateButton.setOnClickListener {
            updateUser()
        }
    }

    private fun openGalleryForImage() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            profilePhotoImageView.setImageURI(selectedImageUri)
        }
    }

    private fun updateUser() {
        val newNickname = nicknameEditText.text.toString()
        val newCareer = careerEditText.text.toString()

        val updatedUser = User(
            nickname = newNickname,
            job = newCareer,
        )
        userViewModel.updateUser(updatedUser, this)
        selectedImageUri?.let { userViewModel.uploadImage(it, this) }

    }

    private fun displayUser(user: FirebaseUser?) {
        if (user != null) {
            userViewModel.getUser(user.uid, this) { user ->
                retrievedUser = user
                if (retrievedUser != null) {
                    nicknameEditText.setText(retrievedUser!!.nickname)
                    careerEditText.setText(retrievedUser!!.job)

                    if (retrievedUser!!.profileImage != null) {
                        Glide.with(this)
                            .load(retrievedUser!!.profileImage.toString())
                            .into(profilePhotoImageView)
                    }
                }
                stopProgressBar()
            }
        }
    }

    private fun signOut() {
        userViewModel.signOut()
        val chatActivity = Intent(this, MainActivity::class.java)
        startActivity(chatActivity)
    }

    private fun redirectToHomepage(user: FirebaseUser?) {
        val chatActivity = Intent(this, MainActivity::class.java)
        chatActivity.putExtra("currentUser", user)
        startActivity(chatActivity)
    }
}

class UserViewModelFactory(var application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(UserRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
