package ge.etsiramua.messangerApp.search

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.chat.ChatActivity
import ge.etsiramua.messangerApp.model.User

class SearchActivity : AppCompatActivity(), SearchAdapter.OnItemClickListener {

    private val handler = Handler()
    private var debounceRunnable: Runnable? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var usersList = emptyList<User>()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(
            this,
            SearchViewModelsFactory(application)
        )[SearchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val searchField = findViewById<EditText>(R.id.search_field)

        recyclerView = findViewById(R.id.friends)
        progressBar = findViewById(R.id.searchProgressBar)

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE

        recyclerView.layoutManager = LinearLayoutManager(this)
        displaySearchResults();
        search(searchField)
    }

    fun onBack(view: View) {
        setResult(Activity.RESULT_OK)
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
                    fetchUsers(namePrefix = searchText)
                }
                handler.postDelayed(debounceRunnable!!, 1000)
            }
        })
    }

    private fun fetchUsers(namePrefix: String) {
        if (namePrefix.length > 2) {
            searchViewModel.getUsersByPrefix(namePrefix, this) { userList ->
                val adapter = SearchAdapter(this, userList, this)
                recyclerView.adapter = adapter
                usersList = userList
            }
        } else {
            val adapter = SearchAdapter(this, emptyList(), this)
            recyclerView.adapter = adapter
            usersList = emptyList()
        }
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun displaySearchResults() {
        val recyclerView: RecyclerView = findViewById(R.id.friends)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchViewModel.getUsersByPrefix("", this) { userList ->
            val adapter = SearchAdapter(this, userList, this)
            recyclerView.adapter = adapter
            usersList = userList

            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun openChatPage(userId: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("currentUser", auth.currentUser)
        intent.putExtra("anotherUserId", userId)
        startActivity(intent)
    }

    override fun onItemClick(position: Int) {
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
