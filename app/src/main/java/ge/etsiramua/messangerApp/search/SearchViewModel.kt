package ge.etsiramua.messangerApp.search

import androidx.lifecycle.ViewModel
import ge.etsiramua.messangerApp.model.User

class SearchViewModel(searchRepository: SearchRepository) : ViewModel() {

    private var repository = searchRepository

    fun getUsersByPrefix(prefix: String, callback: (List<User>) -> Unit) {
        repository.getUsersByPrefix(prefix, callback)
    }
}