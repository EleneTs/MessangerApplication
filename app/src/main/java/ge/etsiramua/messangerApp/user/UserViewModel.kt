package ge.etsiramua.messangerApp.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.model.User

class UserViewModel(val repository: UserRepository) : ViewModel() {
    fun getUser(firebaseUser: FirebaseUser, callback: (User?) -> Unit) {
        repository.getUser(firebaseUser, callback)
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
