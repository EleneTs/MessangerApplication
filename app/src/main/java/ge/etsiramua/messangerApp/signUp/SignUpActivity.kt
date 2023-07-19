package ge.etsiramua.messangerApp.signUp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.main.MainActivity
import ge.etsiramua.messangerApp.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var nickname: EditText
    private lateinit var password: EditText
    private lateinit var job: EditText
    private lateinit var signUpButton: Button


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
            println("nickname error message")
            return
        }

        if (password.text.isEmpty()) {
            println("password error message")
            return
        }

        if (job.text.isEmpty()) {
            println("job error message")
            return
        }

        // sign up logic ...
        signUpViewModel.register(nickname.text.toString(), password.text.toString(), job.text.toString()) { user, exception ->
            if (user != null) {
                openHomePage(user)
                println("User created successfully")
            } else {
                println("Failed to create user: ${exception?.message}")
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
