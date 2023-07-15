package ge.etsiramua.messangerApp.user

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.MainActivity
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.model.User

class ProfileActivity : AppCompatActivity() {
    private lateinit var changePhotoImageView: ImageView
    private lateinit var nicknameEditText: EditText
    private lateinit var careerEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var singOutButton: Button


    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, UserViewModelFactory(application)).get(UserViewModel::class.java)
    }

    private var retrievedUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        changePhotoImageView = findViewById(R.id.profile_photo)
        nicknameEditText = findViewById(R.id.nickname)
        careerEditText = findViewById(R.id.career)

        val user = IntentCompat.getParcelableExtra(
            intent, "currentUser",
            FirebaseUser::class.java
        )
        displayUser(user)

        findViewById<Button>(R.id.sign_out_button).setOnClickListener {
            signOut()
        }

        findViewById<Button>(R.id.update_button).setOnClickListener {
            updateUser()
        }
    }

    private fun updateUser() {
        val newNickname = nicknameEditText.text.toString()
        val newCareer = careerEditText.text.toString()
        if(!(newNickname == retrievedUser!!.nickname && newCareer == retrievedUser!!.nickname)) {
            val updatedUser = User(nickname = newNickname, job = newCareer)
            userViewModel.updateUser(updatedUser)
        }
    }

    private fun displayUser(user: FirebaseUser?) {
        if (user != null) {
            userViewModel.getUser(user) { user ->
                retrievedUser = user
                if (retrievedUser != null) {
                    nicknameEditText.setText(retrievedUser!!.nickname)
                    careerEditText.setText(retrievedUser!!.job)
                }
            }
        }
    }
    private fun signOut() {
        userViewModel.signOut()
        val mainActivity = Intent(this, MainActivity::class.java)
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
