package ge.etsiramua.messangerApp.chat

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ge.etsiramua.messangerApp.model.Message
import ge.etsiramua.messangerApp.user.UserRepository
import java.time.LocalDateTime

class ChatViewModel(val repository: ChatRepository, val userRepository: UserRepository) : ViewModel() {

    fun getAllLastMessages(userId: String, onComplete: (List<Message>?) -> Unit) {
        repository.getAllLastMessages(userId) { messages ->
            onComplete(messages)
        }
    }

    fun getConversation(receiverId: String, senderId: String) {
        repository.getConversation(receiverId, senderId) { messages ->
            // Handle the list of messages here, they will be sorted by time ascending
            println("HERE ARE THE MESSAGES:")
            println(messages)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(receiverId: String, senderId: String, message: String, date: LocalDateTime) {
        repository.sendMessage(receiverId, senderId, message, date)
    }


}

class ChatViewModelFactory(var application: Application) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(ChatRepository(), UserRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}