package ge.etsiramua.messangerApp.chat

import android.app.Application
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.model.Message
import ge.etsiramua.messangerApp.user.UserRepository

class ChatActivity : AppCompatActivity() {

    private var user: FirebaseUser? = null
    private lateinit var nameTextView: TextView
    private lateinit var positionTextView: TextView


    val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(this, ChatViewModelFactory(application)).get(ChatViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        nameTextView = findViewById(R.id.toolbarname)
        positionTextView = findViewById(R.id.toolbarposition)
        displayUser()
    }

    private fun displayUser() {
        val message = IntentCompat.getParcelableExtra(
            intent, "message",
            Message::class.java
        )
        user = IntentCompat.getParcelableExtra(
            intent, "currentUser",
            FirebaseUser::class.java
        )

        chatViewModel.getUser(message!!.senderId!!) { user ->
            if (user != null) {
                nameTextView.text = user.nickname
                positionTextView.text = user.job
            }
        }

        displayChat(message.receiverId, message.senderId)

    }

    private fun displayChat(receiverId: String?, senderId: String?) {
        chatViewModel.getConversation(receiverId!!, senderId!!) {messages ->
            print(messages)
        }
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