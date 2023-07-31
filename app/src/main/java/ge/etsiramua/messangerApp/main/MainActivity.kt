package ge.etsiramua.messangerApp.main

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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

const val REQUEST_CODE_CHILD_ACTIVITY = 1
class MainActivity : AppCompatActivity(), ChatOverviewAdapter.OnItemClickListener {

    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var plusButton: FloatingActionButton
    private lateinit var homeButton: ImageView
    private lateinit var settingsButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private var messagesList: List<Message> = emptyList()
    private var user: FirebaseUser? = null
    private val handler = Handler()
    private var debounceRunnable: Runnable? = null

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

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        progressBar.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
        if (resultCode == Activity.RESULT_OK) {
            user?.let { setUpCurrentChats(it) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUser(): FirebaseUser? {
        user = IntentCompat.getParcelableExtra(
            intent, "currentUser",
            FirebaseUser::class.java
        )
        user?.let { setUpCurrentChats(it) }

        if (user == null)
            openSignInPage()

        return user
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpCurrentChats(user: FirebaseUser) {
        recyclerView.layoutManager = LinearLayoutManager(this)

        getChatList { messages ->
            messagesList = messages!!
            val adapter = messages?.let { ChatOverviewAdapter(user.uid, it, this) }
            recyclerView.adapter = adapter

            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getChatList(onComplete: (List<Message>?) -> Unit) {
        chatViewModel.getAllLastMessages(user!!.uid, this) { lastMessages ->
            if (lastMessages != null) {
                val messages = lastMessages.sortedByDescending { it.timestamp }
                messagesList = messages
                onComplete(messages)
                if (lastMessages.isEmpty()) {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
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
            userViewModel.getUser(anotherUserId!!, this) { sender ->
                if (sender != null) {
                    message.senderName = sender.nickname
                    message.senderPictureUri = sender.profileImage
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
        val searchField = findViewById<EditText>(R.id.main_search_text)
        search(searchField)
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
                startActivityForResult(profileActivity, REQUEST_CODE_CHILD_ACTIVITY)
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
        startActivityForResult(intent, REQUEST_CODE_CHILD_ACTIVITY)
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

    private fun search(searchField: EditText) {
        searchField.addTextChangedListener(object : TextWatcher {
            private var searchFor = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun afterTextChanged(s: Editable?) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim()
                if (searchText == searchFor) {
                    return
                }
                searchFor = searchText
                debounceRunnable?.let { handler.removeCallbacks(it) }
                debounceRunnable = Runnable {
                    val searchText = s?.toString() ?: ""
                    filterUsers(namePrefix = searchText)
                }
                handler.postDelayed(debounceRunnable!!, 1000)
            }
        })
    }

    private fun filterUsers(namePrefix: String) {
        val filteredMessages = messagesList.filter { message ->
            message.receiverName?.startsWith(namePrefix, ignoreCase = true) == true
        }
        val adapter = filteredMessages.let { user?.let { it1 -> ChatOverviewAdapter(it1.uid, it, this) } }
        recyclerView.adapter = adapter
    }
}
