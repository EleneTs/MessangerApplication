package ge.etsiramua.messangerApp.signUp

import androidx.lifecycle.ViewModel

class SignUpViewModel(val repository: SignUpRepository): ViewModel() {
    private var rep = repository

    fun register(nickname: String, password: String, job: String) {
        rep.register(nickname, password, job)
    }
}
