package ge.etsiramua.messangerApp.signIn

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class SignInViewModel(val repository: SignInRepository): ViewModel() {
    fun signIn(name: String, password: String, callback: (FirebaseUser?, Exception?) -> Unit) {
        repository.signIn(name, password, callback)
    }
}
