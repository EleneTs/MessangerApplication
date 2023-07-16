package ge.etsiramua.messangerApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.content.IntentCompat
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.signIn.SignInActivity
import ge.etsiramua.messangerApp.user.ProfileActivity

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
    }

    private fun getUser(): FirebaseUser? {
        val user = IntentCompat.getParcelableExtra(
            intent, "currentUser",
            FirebaseUser::class.java
        )

        if (user == null) {
            openSignInPage()
        }
        return user
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
