package ge.etsiramua.messangerApp.signUp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import ge.etsiramua.messangerApp.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var nickname: EditText
    private lateinit var password: EditText
    private lateinit var job: EditText
    private lateinit var signUpButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            signUpNewUser()
        }
    }

    private fun signUpNewUser() {
        nickname = findViewById<EditText>(R.id.editTextNickname)
        password = findViewById<EditText>(R.id.editTextNickname)
        job = findViewById<EditText>(R.id.editTextNickname)

        if (nickname.text.isEmpty()) {
            println("nickname error message")
        }

        if (password.text.isEmpty()) {
            println("nickname error message")
        }

        // sign up logic ...




    }
}
