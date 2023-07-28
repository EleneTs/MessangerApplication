package ge.etsiramua.messangerApp.chat

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.mikhaellopez.circularimageview.CircularImageView
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.user.UserRepository
import java.time.LocalDateTime

class ChatActivity : AppCompatActivity() {

    private var user: FirebaseUser? = null
    private lateinit var nameTextView: TextView
    private lateinit var positionTextView: TextView
    private lateinit var chatMessages: RecyclerView
    private lateinit var newMessageText: EditText
    private lateinit var currentUserId: String
    private lateinit var anotherUserId: String
    private lateinit var onSendButton: ImageButton
    private lateinit var profilePhotoImageView: CircularImageView


    val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(this, ChatViewModelFactory(application)).get(ChatViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        nameTextView = findViewById(R.id.toolbarname)
        positionTextView = findViewById(R.id.toolbarposition)
        chatMessages = findViewById(R.id.chat_conversations)
        newMessageText = findViewById(R.id.message_edit_text)
        onSendButton = findViewById(R.id.send)
        profilePhotoImageView = findViewById(R.id.chat_profile_pic)

        user = IntentCompat.getParcelableExtra(
            intent, "currentUser",
            FirebaseUser::class.java
        )

        currentUserId = user!!.uid
        anotherUserId = getAnotherUserId()

        onSendButton.setOnClickListener {
            val messageText = newMessageText.text.toString()
            newMessageText.text.clear()

            chatViewModel.sendMessage(anotherUserId, currentUserId, messageText, LocalDateTime.now())

            displayChat(currentUserId, anotherUserId)
        }

        displayUser()
    }

    fun onBack(view: View) {
        finish()
    }

    private fun getAnotherUserId(): String {
        val anotherUserId = intent.getStringExtra(
            "anotherUserId"
        )

        return anotherUserId!!
    }

    private fun displayUser() {

        chatViewModel.getUser(anotherUserId!!, this) { user ->
            if (user != null) {
                nameTextView.text = user.nickname
                positionTextView.text = user.job
            }
            if (user!!.profileImage != null) {
                Glide.with(this)
                    .load(user!!.profileImage.toString())
                    .into(profilePhotoImageView)
            }
        }

        displayChat(currentUserId, anotherUserId)

    }

    private fun displayChat(receiverId: String?, senderId: String?) {
        chatViewModel.getConversation(receiverId!!, senderId!!, this) { messages ->
            val adapter = messages?.let { ChatMessagesAdapter(user!!.uid, it) }
            chatMessages.adapter = adapter

            val lastPosition = (messages?.size ?: 1) - 1
            chatMessages.scrollToPosition(lastPosition)
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
