package ge.etsiramua.messangerApp.model

import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    var id: String? = null,
    val senderId: String? = null,
    val receiverId: String? = null,
    val text: String? = null,
    val timestamp: Long? = null,
    var senderPictureUri: Uri? = null,
    var receiverPictureUri: Uri? = null,
    var senderName: String? = null,
    var receiverName: String? = null
)