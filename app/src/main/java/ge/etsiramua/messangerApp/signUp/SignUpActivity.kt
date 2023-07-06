package ge.etsiramua.messangerApp.signUp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import ge.etsiramua.messangerApp.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var nickname: EditText
    private lateinit var password: EditText
    private lateinit var job: EditText
    private lateinit var signUpButton: Button

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

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
        password = findViewById(R.id.editTextNickname)
        job = findViewById(R.id.editTextNickname)

        if (nickname.text.isEmpty()) {
            println("nickname error message")
        }

        if (password.text.isEmpty()) {
            println("nickname error message")
        }

        // sign up logic ...

        auth.createUserWithEmailAndPassword(nickname.text.toString(),password.text.toString()).addOnCompleteListener {
            if (it.isSuccessful) {
                println("ADDED USER")
            } else {

            }

        }

    }
}
