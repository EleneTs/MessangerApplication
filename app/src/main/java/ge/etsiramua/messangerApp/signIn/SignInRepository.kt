package ge.etsiramua.messangerApp.signIn

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignInRepository {

    private val EMAIL_SUFIX = "@messenger.com"

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(nickname: String, password: String,  callback: (FirebaseUser?, Exception?) -> Unit) {
        val email = formatNickname(nickname)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(auth.currentUser, null)
            } else {
                val exception = task.exception
                callback(null, exception)
            }
        }
    }
    private fun formatNickname(nickname: String): String {
        return "$nickname$EMAIL_SUFIX"
    }
}
