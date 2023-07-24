package ge.etsiramua.messangerApp.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.ChatOverviewAdapter
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.chat.ChatActivity
import ge.etsiramua.messangerApp.chat.ChatViewModel
import ge.etsiramua.messangerApp.chat.ChatViewModelFactory
import ge.etsiramua.messangerApp.model.Message
import ge.etsiramua.messangerApp.search.SearchActivity
import ge.etsiramua.messangerApp.signIn.SignInActivity
import ge.etsiramua.messangerApp.user.ProfileActivity
import ge.etsiramua.messangerApp.user.UserViewModel
import ge.etsiramua.messangerApp.user.UserViewModelFactory
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), ChatOverviewAdapter.OnItemClickListener {

    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var plusButton: FloatingActionButton
    private lateinit var homeButton: ImageView
    private lateinit var settingsButton: ImageView
    private var messagesList: List<Message> = emptyList()
    private var user: FirebaseUser? = null

    val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(this, ChatViewModelFactory(application)).get(ChatViewModel::class.java)
    }

    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, UserViewModelFactory(application)).get(UserViewModel::class.java)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()

        user = getUser()
        addListeners(user)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUser(): FirebaseUser? {
        user = IntentCompat.getParcelableExtra(
            intent, "currentUser",
            FirebaseUser::class.java
        )

        user?.let { setUpCurrentChats(it) }

        if (user == null) {
            openSignInPage()
        }

        return user
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpCurrentChats(user: FirebaseUser) {
        val recyclerView: RecyclerView = findViewById(R.id.lastConversations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val chatList = getChatList { messages ->
            messagesList = messages!!
            val adapter = messages?.let { ChatOverviewAdapter(user.uid, it, this) }
            recyclerView.adapter = adapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getChatList(onComplete: (List<Message>?) -> Unit) {

        val id1 = "D7Ib8seNZXbdftKkBtkwPTqPCSF2"
        val id2 = "KUthvSYXxMfxn3TCukvtgsk2a8m2"

        chatViewModel.sendMessage(senderId = id1, receiverId = user!!.uid, message = "Message text rggggggggg ggggggggggg ggggggggg gggggggg ggggggg gggggggg ggggggggg gggggg 3  min ago.", date = LocalDateTime.now().minusMinutes(3))
        chatViewModel.sendMessage(senderId = id2, receiverId = user!!.uid, message = "Message text 1 min ago.", date = LocalDateTime.now().minusMinutes(1))
        chatViewModel.sendMessage(senderId = user!!.uid, receiverId = id2, message = "Message texdddd ddddddddddd ddddddddddd dddddddddddddd dddddd dddddddd ddddddddddd ddddddd ddddddddt now.", date = LocalDateTime.now())

        chatViewModel.getAllLastMessages(user!!.uid) { lastMessages ->
            if (lastMessages != null) {
                val messages = lastMessages.sortedByDescending { it.timestamp }
                messagesList = messages
                onComplete(messages)

            }
        }
    }

    private fun updateSenderUserInfo(messages: List<Message>, onComplete: (List<Message>?) -> Unit) {
        val updatedMessages =  ArrayList<Message>()
        for (message in messages) {
            var anotherUserId = message.senderId
            if (message.senderId == user!!.uid) {
                anotherUserId = message.receiverId
            }
            val senderUser = userViewModel.getUser(anotherUserId!!) { sender ->
                if (sender != null) {
                    message.senderName = sender.nickname
                    message.senderPictureUri = sender.profileImage
                    println(message)
                    updatedMessages.add(message)
                }
            }
        }
        messagesList - messages
        onComplete(updatedMessages)
    }

    private fun initViews() {
        bottomAppBar = findViewById(R.id.bottom_toolbar)
        plusButton = findViewById(R.id.plus_button)
        homeButton = findViewById(R.id.home_button)
        settingsButton = findViewById(R.id.settings_button)
    }

    private fun addListeners(user: FirebaseUser?) {
        plusButton.setOnClickListener {
            openSearchPage();
        }

        homeButton.setOnClickListener {

        }

        settingsButton.setOnClickListener {
            val currentUser = IntentCompat.getParcelableExtra(
                intent, "currentUser",
                FirebaseUser::class.java
            )

            if (currentUser != null) {
                val profileActivity = Intent(this, ProfileActivity::class.java)
                profileActivity.putExtra("currentUser", user)
                startActivity(profileActivity)
            }
        }
    }

    private fun openSignInPage() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    private fun openSearchPage() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun openChatPage(message: Message) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("currentUser", user)
        intent.putExtra("message", message)
        startActivity(intent)
    }

    override fun onItemClick(position: Int) {
        openChatPage(messagesList[position])
    }
}
