package ge.etsiramua.messangerApp.signUp

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class SignUpViewModel(val repository: SignUpRepository) : ViewModel() {

    private var rep = repository

    fun register(nickname: String, password: String, job: String, callback: (FirebaseUser?, Exception?) -> Unit) {
        rep.register(nickname, password, job, callback)
    }
}
