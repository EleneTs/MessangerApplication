package ge.etsiramua.messangerApp.search

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ge.etsiramua.messangerApp.R
import ge.etsiramua.messangerApp.model.User


class SearchActivity : AppCompatActivity() {

    private val handler = Handler()
    private var debounceRunnable: Runnable? = null
    private lateinit var recyclerView: RecyclerView

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
                val adapter = SearchAdapter(this, userList)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun displaySearchResults() {
        val recyclerView: RecyclerView = findViewById(R.id.friends)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val chatList = createUsersList() // Replace this with your actual data source

    }

    fun createUsersList(): List<User> {
        val userList = mutableListOf<User>()
        userList.add(User("1", "John", "Engineer", Uri.parse("content://john_profile_image")))
        userList.add(User("2", "Alice", "Doctor", Uri.parse("content://alice_profile_image")))
        userList.add(User("3", "Bob", "Teacher", Uri.parse("content://bob_profile_image")))

        return userList
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