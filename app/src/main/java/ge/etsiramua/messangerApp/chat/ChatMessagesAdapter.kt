package ge.etsiramua.messangerApp.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.model.Message
import java.text.SimpleDateFormat
import java.util.*

class ChatMessagesAdapter(var currentUserId: String, var messages: List<Message>): RecyclerView.Adapter<ChatMessageHolder>()  {

    private fun viewHolderOfType0(parent: ViewGroup): ChatMessageHolder {
        return ChatMessageHolder(LayoutInflater.from(parent.context).inflate(R.layout.send_message, parent, false))
    }

    private fun viewHolderOfType1(parent: ViewGroup): ChatMessageHolder {
        return ChatMessageHolder(LayoutInflater.from(parent.context).inflate(R.layout.receive_message, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (currentUserId == message.senderId) {
            0
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageHolder {
        return if (viewType == 0) {
            viewHolderOfType0(parent)
        } else {
            viewHolderOfType1(parent)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ChatMessageHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.text
        val date = message.timestamp?.formatTimestamp()
        holder.date.text = date
    }

    private fun Long.formatTimestamp(): String {
        val currentTime = System.currentTimeMillis() / 1000
        val differenceInSeconds = currentTime - this

        return if (differenceInSeconds < 24 * 60 * 60) {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            dateFormat.format(Date(this * 1000))
        } else {
            val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
            dateFormat.format(Date(this * 1000))
        }
    }

}

class ChatMessageHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var messageText: TextView = itemView.findViewById(R.id.chat_message)
    var date: TextView = itemView.findViewById(R.id.chat_message_time)
}
