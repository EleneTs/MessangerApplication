package ge.etsiramua.messangerApp.user

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.mikhaellopez.circularimageview.CircularImageView
import ge.etsiramua.messangerApp.MainActivity
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.model.User

class ProfileActivity : AppCompatActivity() {
    companion object {
        private const val GALLERY_REQUEST_CODE = 123
    }

    private lateinit var nicknameEditText: EditText
    private lateinit var careerEditText: EditText
    private lateinit var homeButton: ImageView
    private lateinit var settingsButton: ImageView
    private lateinit var profilePhotoImageView: CircularImageView

    private var selectedImageUri: Uri? = null
    private var retrievedUser: User? = null
    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, UserViewModelFactory(application)).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews()

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
    }

    private fun setupListeners(user: FirebaseUser?) {
        homeButton.setOnClickListener {
            redirectToHomepage(user)
        }

        profilePhotoImageView.setOnClickListener {
            openGalleryForImage()
        }

        findViewById<Button>(R.id.sign_out_button).setOnClickListener {
            signOut()
        }

        findViewById<Button>(R.id.update_button).setOnClickListener {
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
        userViewModel.updateUser(updatedUser)
        selectedImageUri?.let { userViewModel.uploadImage(it) }

    }

    private fun displayUser(user: FirebaseUser?) {
        if (user != null) {
            userViewModel.getUser(user) { user ->
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
            }
        }
    }

    private fun signOut() {
        userViewModel.signOut()
        val mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
    }

    private fun redirectToHomepage(user: FirebaseUser?) {
        val mainActivity = Intent(this, MainActivity::class.java)
        mainActivity.putExtra("currentUser", user)
        startActivity(mainActivity)
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
