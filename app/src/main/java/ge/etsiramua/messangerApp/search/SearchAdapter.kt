package ge.etsiramua.messangerApp.search

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularimageview.CircularImageView
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.model.User
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SearchAdapter(
    private val context: Context,
    private val chatList: List<User>,
) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>(){


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return SearchViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val chatItem = chatList[position]
        holder.job.text = chatItem.job
        holder.name.text = chatItem.nickname
        if(chatItem.profileImage != null ){
            Glide.with(context)
                .load(chatItem.profileImage.toString())
                .into(holder.senderPicture)
        }
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

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val job: TextView = itemView.findViewById(R.id.job)
        val name: TextView = itemView.findViewById(R.id.name)
        val senderPicture: CircularImageView = itemView.findViewById(R.id.profile_picture)
    }
}