package ge.etsiramua.messangerApp.chat

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import ge.etsiramua.messangerApp.model.Message
import ge.etsiramua.messangerApp.model.User
import ge.etsiramua.messangerApp.user.UserRepository
import java.time.LocalDateTime

class ChatViewModel(private val repository: ChatRepository, private val userRepository: UserRepository) : ViewModel() {
    fun getAllLastMessages(userId: String ,context: Context, onComplete: (List<Message>?) -> Unit) {
        repository.getAllLastMessages(userId, context) { messages ->
            onComplete(messages)
        }
    }

    fun getConversation(receiverId: String, senderId: String, context: Context, onComplete: (List<Message>?) -> Unit) {
        repository.getConversation(receiverId, senderId, context) { messages ->
            onComplete(messages)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(receiverId: String, senderId: String, message: String, date: LocalDateTime) {
        repository.sendMessage(receiverId, senderId, message, date)
    }

    fun getUser(firebaseUserId: String, context: Context, callback: (User?) -> Unit) {
        userRepository.getUser(firebaseUserId, context, callback)
    }
}
