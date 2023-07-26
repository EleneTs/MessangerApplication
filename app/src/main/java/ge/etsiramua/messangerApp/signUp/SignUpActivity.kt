package ge.etsiramua.messangerApp.signUp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.main.MainActivity
import ge.etsiramua.messangerApp.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var nickname: EditText
    private lateinit var password: EditText
    private lateinit var job: EditText

    val signUpViewModel: SignUpViewModel by lazy {
        ViewModelProvider(this, SignUpViewModelsFactory(application)).get(SignUpViewModel::class.java)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            signUpNewUser()
        }
    }

    private fun signUpNewUser() {
        nickname = findViewById(R.id.editTextNickname)
        password = findViewById(R.id.editTextPassword)
        job = findViewById(R.id.editTextJob)

        if (nickname.text.isEmpty()) {
            Toast.makeText(this, "Please enter a nickname.", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.text.isEmpty()) {
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show()
            return
        }

        if (job.text.isEmpty()) {
            Toast.makeText(this, "Please enter a job.", Toast.LENGTH_SHORT).show()
            return
        }

        signUpViewModel.register(nickname.text.toString(), password.text.toString(), job.text.toString()) { user, exception ->
            if (user != null) {
                openHomePage(user)
            } else {
                Toast.makeText(this, "Failed to create user" + " " + exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openHomePage(user: FirebaseUser) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("currentUser", user)
        startActivity(intent)
    }
}

class SignUpViewModelsFactory(var application: Application) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(SignUpRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
