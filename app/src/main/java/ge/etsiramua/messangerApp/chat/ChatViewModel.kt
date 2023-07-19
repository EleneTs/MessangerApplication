package ge.etsiramua.messangerApp.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import ge.etsiramua.messangerApp.model.Message
import ge.etsiramua.messangerApp.model.User
import ge.etsiramua.messangerApp.user.UserRepository
import java.time.LocalDateTime

class ChatViewModel(val repository: ChatRepository, val userRepository: UserRepository) : ViewModel() {

    fun getAllLastMessages(userId: String, onComplete: (List<Message>?) -> Unit) {
        repository.getAllLastMessages(userId) { messages ->
            onComplete(messages)
        }
    }

    fun getConversation(receiverId: String, senderId: String,  onComplete: (List<Message>?) -> Unit) {
        repository.getConversation(receiverId, senderId) { messages ->
            onComplete(messages)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(receiverId: String, senderId: String, message: String, date: LocalDateTime) {
        repository.sendMessage(receiverId, senderId, message, date)
    }

    fun getUser(firebaseUserId: String, callback: (User?) -> Unit) {
        userRepository.getUser(firebaseUserId, callback)
    }
}