package ge.etsiramua.messangerApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.IntentCompat
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.signIn.SignInActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = IntentCompat.getParcelableExtra(intent, "currentUser",
            FirebaseUser::class.java)

        if (user == null) {
            openSignInPage()
        }
    }

    private fun openSignInPage() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
}
