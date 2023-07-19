package ge.etsiramua.messangerApp.chat

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ge.etsiramua.messangerApp.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val nameTextView: TextView = findViewById(R.id.toolbarname)
        val positionTextView: TextView = findViewById(R.id.toolbarposition)

        val name = "John Doe"
        val position = "Software Engineer"

        // Set the values to the TextViews
        nameTextView.text = name
        positionTextView.text = position
    }
}