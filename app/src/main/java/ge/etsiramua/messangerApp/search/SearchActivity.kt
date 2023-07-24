package ge.etsiramua.messangerApp.search

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ge.etsiramua.messangerApp.ChatOverviewAdapter
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.chat.ChatActivity
import ge.etsiramua.messangerApp.model.Message
import ge.etsiramua.messangerApp.model.User


class SearchActivity : AppCompatActivity(), SearchAdapter.OnItemClickListener  {

    private val handler = Handler()
    private var debounceRunnable: Runnable? = null
    private lateinit var recyclerView: RecyclerView
    private var usersList = emptyList<User>()

//    private var user: FirebaseUser? = IntentCompat.getParcelableExtra(
//        intent, "currentUser",
//        FirebaseUser::class.java
//    )

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(this, SearchViewModelsFactory(application)).get(SearchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val searchField = findViewById<EditText>(R.id.search_field)
        recyclerView = findViewById(R.id.friends)
        recyclerView.layoutManager = LinearLayoutManager(this)
        displaySearchResults();
        search(searchField)
    }

    fun onBack(view: View) {
        finish()
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
                    println("User typed: $searchText")
                    fetchUsers(namePrefix = searchText)
                }
                handler.postDelayed(debounceRunnable!!, 1000)
            }
        })
    }

    private fun fetchUsers(namePrefix: String) {
        if (namePrefix.length > 2) {
            searchViewModel.getUsersByPrefix(namePrefix) { userList ->
                val adapter = SearchAdapter(this, userList, this)
                recyclerView.adapter = adapter
                usersList = userList
            }
        }
    }

    private fun displaySearchResults() {
        val recyclerView: RecyclerView = findViewById(R.id.friends)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchViewModel.getUsersByPrefix("") { userList ->
            val adapter = SearchAdapter(this, userList, this)
            recyclerView.adapter = adapter
            usersList = userList
        }
    }

    private fun openChatPage(userId: String) {
        println("open chat page for id: $userId")
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("currentUser", auth.currentUser)
        intent.putExtra("anotherUserId", userId)
        startActivity(intent)
    }

    override fun onItemClick(position: Int) {
        println("Opening chat for position $position")
        usersList[position].id?.let { openChatPage(it) }
    }
}

class SearchViewModelsFactory(var application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(SearchRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}