package ge.etsiramua.messangerApp.main

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import java.util.*

class MainActivity : AppCompatActivity(), ChatOverviewAdapter.OnItemClickListener {

    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var plusButton: FloatingActionButton
    private lateinit var homeButton: ImageView
    private lateinit var settingsButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private var messagesList: List<Message> = emptyList()
    private var user: FirebaseUser? = null

    val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(this, ChatViewModelFactory(application))[ChatViewModel::class.java]
    }

    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(this, UserViewModelFactory(application))[UserViewModel::class.java]
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.lastConversations)

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE

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
        recyclerView.layoutManager = LinearLayoutManager(this)

        val chatList = getChatList { messages ->
            messagesList = messages!!
            val adapter = messages?.let { ChatOverviewAdapter(user.uid, it, this) }
            recyclerView.adapter = adapter

            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getChatList(onComplete: (List<Message>?) -> Unit) {

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
            it.foregroundTintList = ColorStateList.valueOf(resources.getColor(R.color.black))
            settingsButton.foregroundTintList = ColorStateList.valueOf(resources.getColor(R.color.blue))
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
            it.foregroundTintList = ColorStateList.valueOf(resources.getColor(R.color.blue))
            settingsButton.foregroundTintList = ColorStateList.valueOf(resources.getColor(R.color.black))
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
        val currentUserId = user!!.uid

        var anotherUserId = message.senderId
        if (message.senderId == currentUserId) {
            anotherUserId = message.receiverId
        }
        intent.putExtra("anotherUserId", anotherUserId)
        startActivity(intent)
    }

    override fun onItemClick(position: Int) {
        openChatPage(messagesList[position])
    }
}
