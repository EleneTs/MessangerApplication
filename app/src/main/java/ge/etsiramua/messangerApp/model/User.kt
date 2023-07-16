package ge.etsiramua.messangerApp.model

import android.net.Uri

data class User(
    var id: String? = null,
    val nickname: String? = null,
    val job: String? = null,
    var profileImage: Uri? = null
)