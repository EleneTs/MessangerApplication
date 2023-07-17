package ge.etsiramua.messangerApp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.IntentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.model.Message
import ge.etsiramua.messangerApp.model.User
import ge.etsiramua.messangerApp.search.SearchActivity
import ge.etsiramua.messangerApp.signIn.SignInActivity
import ge.etsiramua.messangerApp.user.ProfileActivity
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var plusButton: FloatingActionButton
    private lateinit var homeButton: ImageView
    private lateinit var settingsButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()

        val user = getUser()
        addListeners(user)

//        val intent = Intent(this, SearchActivity::class.java)
//        startActivity(intent)
    }

    private fun getUser(): FirebaseUser? {
        val user = IntentCompat.getParcelableExtra(
            intent, "currentUser",
            FirebaseUser::class.java
        )

        if (user != null) {
            setUpCurrentChats(user)
        }

        if (user == null) {
            openSignInPage()
        }

        return user
    }

    private fun setUpCurrentChats(user: FirebaseUser) {
        val recyclerView: RecyclerView = findViewById(R.id.lastConversations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val chatList = getDummyChatList() // Replace this with your actual data source
        val adapter = ChatOverviewAdapter(chatList)
        recyclerView.adapter = adapter


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDummyChatList(): List<Message> {
        val dummyList = ArrayList<Message>()
        dummyList.add(Message(from = "Michael Scott", text = "That's What She Said", date = LocalDateTime.now().minusMinutes(5)))
        dummyList.add(Message(from = "Elene Elene", text = "Whats uup", date = LocalDateTime.now().minusHours(2)))
        dummyList.add(Message(from = "Nika Nika", text = "Ok lets hang out", date = LocalDateTime.now().minusHours(3)))
        dummyList.add(Message(from = "Nika Nika", text = "Ok lets hang out", date = LocalDateTime.now().minusHours(3)))
        dummyList.add(Message(from = "Nika Nika", text = "Ok lets hang out", date = LocalDateTime.now().minusHours(3)))
        dummyList.add(Message(from = "Nika Nika", text = "Ok lets hang out", date = LocalDateTime.now().minusHours(3)))
        dummyList.add(Message(from = "Nika Nika", text = "Ok lets hang out", date = LocalDateTime.now().minusDays(3)))

        // Add more dummy chat items here

        return dummyList
    }

    private fun initViews() {
        bottomAppBar = findViewById(R.id.bottom_toolbar)
        plusButton = findViewById(R.id.plus_button)
        homeButton = findViewById(R.id.home_button)
        settingsButton = findViewById(R.id.settings_button)
    }

    private fun addListeners(user: FirebaseUser?) {
        plusButton.setOnClickListener {

        }

        homeButton.setOnClickListener {

        }

        settingsButton.setOnClickListener {
            val currentUser = IntentCompat.getParcelableExtra(
                intent, "currentUser",
                FirebaseUser::class.java
            )

            if (currentUser != null) {
                print(currentUser)
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
}
