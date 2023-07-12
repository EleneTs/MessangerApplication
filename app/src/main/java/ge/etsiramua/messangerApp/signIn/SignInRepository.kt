package ge.etsiramua.messangerApp.signIn

import com.google.firebase.auth.FirebaseAuth

class SignInRepository {

    private val EMAIL_SUFIX = "@messenger.com"

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(nickname: String, password: String) {
        val email = formatNickname(nickname)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                println("USER SIGNED IN")
            }
        }
    }

    private fun formatNickname(nickname: String): String {
        return "$nickname$EMAIL_SUFIX"
    }
}