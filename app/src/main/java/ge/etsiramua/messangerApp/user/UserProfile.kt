package ge.etsiramua.messangerApp.user

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ge.etsiramua.messangerApp.R


class ProfileActivity : AppCompatActivity() {
    private lateinit var changePhotoImageView: ImageView
    private lateinit var nicknameEditText: EditText
    private lateinit var careerEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        changePhotoImageView = findViewById(R.id.profile_photo)
        nicknameEditText = findViewById(R.id.nickname)
        careerEditText = findViewById(R.id.career)

    }
}

