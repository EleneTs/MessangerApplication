package ge.etsiramua.messangerApp.signUp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ge.etsiramua.messangerApp.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var nickname: EditText
    private lateinit var password: EditText
    private lateinit var job: EditText
    private lateinit var signUpButton: Button

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val EMAIL_SUFIX = "@messenger.com"

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
        }
        val email = formatNickname(nickname.text.toString())

        if (password.text.isEmpty()) {
            println("nickname error message")
        }

        if (job.text.isEmpty()) {
            println("job error message")
        }

        // sign up logic ...

        auth.createUserWithEmailAndPassword(email,password.text.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                println("ADDED USER")
            } else {

            }

        }

    }

    private fun formatNickname(nickname: String): String {
        return "$nickname$EMAIL_SUFIX"
    }
}
