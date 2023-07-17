package ge.etsiramua.messangerApp.model

import com.google.firebase.database.IgnoreExtraProperties
import java.time.LocalDateTime
import java.util.*


@IgnoreExtraProperties
data class Message(
    var id: String? = null,
    val from: String? = null,
    val to: String? = null,
    val text: String? = null,
    val date: LocalDateTime? = null,
)