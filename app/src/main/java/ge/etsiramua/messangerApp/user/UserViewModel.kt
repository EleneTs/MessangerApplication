package ge.etsiramua.messangerApp.user

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import ge.etsiramua.messangerApp.model.User

class UserViewModel(val repository: UserRepository) : ViewModel() {
    fun getUser(firebaseUserId: String, context: Context, callback: (User?) -> Unit) {
        repository.getUser(firebaseUserId, context, callback)
    }

    fun updateUser(user: User, context: Context) {
        repository.updateUser(user, context)
    }

    fun signOut() {
        repository.signOut()
    }

    fun uploadImage(changedPhotoFilepath: Uri, context: Context) {
        repository.uploadImage(changedPhotoFilepath, context)
    }
}
