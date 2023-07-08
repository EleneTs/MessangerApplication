package ge.etsiramua.messangerApp.signIn

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.signUp.SignUpActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var nickname: EditText
    private lateinit var password: EditText

    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button

    private val EMAIL_SUFIX = "@messenger.com"

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

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
        val email = formatNickname(nickname.text.toString())

        if (password.text.isEmpty()) {
            println("nickname error message")
        }

        // sign in logic ...
        auth.signInWithEmailAndPassword(email, password.text.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                println("USER SIGNED IN")
            }
        }
    }

    private fun formatNickname(nickname: String): String {
        return "$nickname$EMAIL_SUFIX"
    }
}

class ViewModelFactory(var application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(SignInRepository())
             as T
        }
        throw IllegalArgumentException("ViewModel class not detected")
    }


}

