package ge.etsiramua.messangerApp

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import ge.etsiramua.messangerApp.model.Message
import java.time.*
import java.time.format.DateTimeFormatter

class ChatOverviewAdapter(
    private val chatList: List<Message>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<ChatOverviewAdapter.ChatViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

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
        holder.textViewSender.text = chatItem.senderName
        holder.textViewLastMessage.text = chatItem.text
        val date = chatItem.timestamp?.let { calculateDate(it) }
        holder.sendDate.text = date
        if (chatItem.senderPictureUri != null) {
            Glide.with(holder.itemView.context)
                .load(chatItem.senderPictureUri.toString())
                .into(holder.senderPicture)
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDate(timestamp: Long): String {
        val dateTime = timestamp.toLocalDateTime()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Long.toLocalDateTime(): LocalDateTime {
        val instant = Instant.ofEpochSecond(this)
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSender: TextView = itemView.findViewById(R.id.message_sender_name)
        val textViewLastMessage: TextView = itemView.findViewById(R.id.last_message)
        val sendDate: TextView = itemView.findViewById(R.id.last_message_date)
        val senderPicture: CircularImageView = itemView.findViewById(R.id.profile_picture)
    }

}