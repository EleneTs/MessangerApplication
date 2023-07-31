package ge.etsiramua.messangerApp.signUp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.etsiramua.messangerApp.model.User

class SignUpRepository {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val users = Firebase.database.getReference("users")

    private val EMAIL_SUFIX = "@messenger.com"

    fun register(
        nickname: String,
        password: String,
        job: String,
        callback: (FirebaseUser?, Exception?) -> Unit
    ) {
        val email = formatNickname(nickname)
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val newUser = User(nickname = nickname, job = job, id = firebaseAuth.currentUser?.uid!!)
                users.child(firebaseAuth.currentUser?.uid!!).setValue(newUser)
                callback(firebaseAuth.currentUser, null)
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
