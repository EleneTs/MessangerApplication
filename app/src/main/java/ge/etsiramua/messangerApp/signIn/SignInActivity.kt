package ge.etsiramua.messangerApp.signIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.signUp.SignUpActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var nickname: EditText
    private lateinit var password: EditText

    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button

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
        nickname = findViewById<EditText>(R.id.editTextNickname)
        password = findViewById<EditText>(R.id.editTextNickname)

        if (nickname.text.isEmpty()) {
            println("nickname error message")
        }

        if (password.text.isEmpty()) {
            println("nickname error message")
        }

        // sign in logic ...
    }
}
