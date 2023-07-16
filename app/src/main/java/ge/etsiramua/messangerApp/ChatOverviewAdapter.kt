package ge.etsiramua.messangerApp

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.mikhaellopez.circularimageview.CircularImageView
import ge.etsiramua.messangerApp.model.Message
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatOverviewAdapter(private val chatList: List<Message>) :
    RecyclerView.Adapter<ChatOverviewAdapter.ChatViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.conversation_item, parent, false)
        return ChatViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]
        holder.textViewSender.text = chatItem.from
        holder.textViewLastMessage.text = chatItem.text
        val date = chatItem.date?.let { calculateDate(it) }
        holder.sendDate.text = date
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDate(dateTime: LocalDateTime): String {
        val currentDateTime = LocalDateTime.now()
        val duration = Duration.between(dateTime, currentDateTime)


        return when {
            duration.toMinutes() < 60 -> "${duration.toMinutes()} min"
            duration.toHours() < 24 -> "${duration.toHours()} hour"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("d MMM")
                return dateTime.format(formatter)
            }
        }
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSender: TextView = itemView.findViewById(R.id.message_sender_name)
        val textViewLastMessage: TextView = itemView.findViewById(R.id.last_message)
        val sendDate: TextView = itemView.findViewById(R.id.last_message_date)
        val senderPicture: CircularImageView = itemView.findViewById(R.id.profile_picture)
    }
}