package ge.etsiramua.messangerApp.signUp

import android.annotation.SuppressLint
import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
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
        }

        if (password.text.isEmpty()) {
            println("nickname error message")
        }

        if (job.text.isEmpty()) {
            println("job error message")
        }

        // sign up logic ...
        signUpViewModel.register(nickname.text.toString(),password.text.toString(), job.text.toString())
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
