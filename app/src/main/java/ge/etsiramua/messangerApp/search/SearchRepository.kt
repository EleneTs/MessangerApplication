package ge.etsiramua.messangerApp.search

import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import ge.etsiramua.messangerApp.model.User

class SearchRepository {

    private val usersReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")
    private var firebaseStorage = FirebaseStorage.getInstance()
    private var storageReference = firebaseStorage.reference

    fun getUsersByPrefix(prefix: String, callback: (List<User>) -> Unit) {
        val usersQuery: Query = usersReference.orderByChild("nickname")
            .startAt(prefix).endAt("$prefix\uf8ff")

        usersQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usersList = mutableListOf<User>()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        if (it.nickname?.startsWith(prefix) == true) {
                            usersList.add(it)
                        }
                    }
                }

                val profileImageFetchTasks = usersList.map { user ->
                    val profileImageRef = storageReference.child("profile_images/profile_image_${user.id}")
                    profileImageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            user.profileImage = uri
                        }
                        .addOnFailureListener { exception ->
                            user.profileImage = null
                        }
                }

                Tasks.whenAllComplete(profileImageFetchTasks).addOnCompleteListener {
                    callback(usersList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(emptyList())
            }
        })
    }
}
