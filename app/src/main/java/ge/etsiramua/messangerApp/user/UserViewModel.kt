package ge.etsiramua.messangerApp.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume

class UserViewModel(val repository: UserRepository) : ViewModel() {
//    fun getUser(firebaseUser: FirebaseUser, callback: (User?) -> Unit) {
//        repository.getUser(firebaseUser, callback)
//    }

    fun getUser(firebaseUserId: String, callback: (User?) -> Unit) {
        repository.getUser(firebaseUserId, callback)
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
    }

    fun signOut() {
        repository.signOut()
    }

    fun uploadImage(changedPhotoFilepath: Uri) {
        repository.uploadImage(changedPhotoFilepath)
    }
}

