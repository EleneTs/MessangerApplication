package ge.etsiramua.messangerApp.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import ge.etsiramua.messangerApp.model.Message
import ge.etsiramua.messangerApp.model.User
import ge.etsiramua.messangerApp.user.UserViewModel
import ge.etsiramua.messangerApp.user.UserViewModelFactory
import java.time.LocalDateTime
import java.time.ZoneOffset

class ChatRepository {

    private val messagesReferences: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("messages")


    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun getAllLastMessages(userId: String, onComplete: (List<Message>) -> Unit) {
        val query = messagesReferences.orderByChild("timestamp")

        val lastMessagesMap = mutableMapOf<String, Message>()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        if (message.receiverId == userId || message.senderId == userId) {

                            var anotherUserId = message.senderId
                            if (message.senderId == userId) {
                                anotherUserId = message.receiverId
                            }
                            if (!lastMessagesMap.containsKey(anotherUserId) || message.timestamp!! > lastMessagesMap[anotherUserId]!!.timestamp!!) {

                                lastMessagesMap[anotherUserId!!] = message
                            }
                        }
                    }
                }

                val lastMessagesList = lastMessagesMap.values.toList()

                onComplete(lastMessagesList)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
//
    }


    fun getConversation(receiverId: String, senderId: String, onComplete: (List<Message>) -> Unit) {
        val query = messagesReferences.orderByChild("timestamp")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()

                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)

                    if (message != null && isMessageBetweenUsers(message, receiverId, senderId)) {
                        messages.add(message)
                    }
                }

                onComplete(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(
        receiverId: String,
        senderId: String,
        messageText: String,
        date: LocalDateTime,
    ) {
        val message = Message(
            senderId = senderId,
            receiverId = receiverId,
            text = messageText,
            timestamp = date.toTimestamp()
        )
//        var dateString = date.toString().replace("-", "")
//        dateString = dateString.replace(":", "")
//        dateString = dateString.replace(".", "")

        val uniqueId = senderId + receiverId + message.timestamp
        messagesReferences.child(uniqueId).setValue(message)
    }

    private fun isMessageBetweenUsers(message: Message, user1Id: String, user2Id: String): Boolean {
        return (message.senderId == user1Id && message.receiverId == user2Id)
                || (message.senderId == user2Id && message.receiverId == user1Id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun LocalDateTime.toTimestamp(): Long {
        return this.toInstant(ZoneOffset.UTC).epochSecond
    }


}