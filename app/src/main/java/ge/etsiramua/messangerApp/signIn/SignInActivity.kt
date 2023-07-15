package ge.etsiramua.messangerApp.signIn

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.MainActivity
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.signUp.SignUpActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var nickname: EditText
    private lateinit var password: EditText

    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button

    val signInViewModel: SignInViewModel by lazy {
        ViewModelProvider(this, SignInViewModelsFactory(application)).get(SignInViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        findViewById<Button>(R.id.signUpButtonSignInPage).setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.signInButton).setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        nickname = findViewById(R.id.signInNickname)
        password = findViewById(R.id.signInPassword)

        if (nickname.text.isEmpty()) {
            println("nickname error message")
        }

        if (password.text.isEmpty()) {
            println("nickname error message")
        }
        // sign in logic ...
        signInViewModel.signIn(nickname.text.toString(), password.text.toString())  { user, exception ->
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

class SignInViewModelsFactory(var application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(SignInRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
