package ge.etsiramua.messangerApp.search

import android.content.Context
import androidx.lifecycle.ViewModel
import ge.etsiramua.messangerApp.model.User
class SearchViewModel(searchRepository: SearchRepository) : ViewModel() {

    private var repository = searchRepository

    fun getUsersByPrefix(prefix: String, context: Context, callback: (List<User>) -> Unit) {
        repository.getUsersByPrefix(prefix, context, callback)
    }
}
