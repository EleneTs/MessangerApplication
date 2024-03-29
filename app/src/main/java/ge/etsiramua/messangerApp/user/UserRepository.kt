package ge.etsiramua.messangerApp.user

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ge.etsiramua.messangerApp.model.User

class UserRepository {
    private val EMAIL_SUFIX = "@messenger.com"
    private val usersReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("users")
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseStorage = FirebaseStorage.getInstance()
    private var storageReference = firebaseStorage.reference

    fun getUser(firebaseUserId: String, context: Context, callback: (User?) -> Unit) {
        val userReference = usersReference.child(firebaseUserId)

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null ) {
                    val profileImageRef = storageReference.child("profile_images/profile_image_${firebaseUserId}")
                    profileImageRef.downloadUrl
                        .addOnSuccessListener { uri ->
                            user.profileImage = uri
                            callback(user)
                        }
                        .addOnFailureListener { exception ->
                            user.profileImage = null
                            callback(user)
//                            Toast.makeText(context, "Failed to fetch profile image for ${user.nickname}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    callback(null)
                    Toast.makeText(context, "Failed to fetch user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Failed to fetch user", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    fun updateUser(user: User, context: Context) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val email = user.nickname?.let { formatNickname(it) }

        if (email != null) {
            firebaseUser?.updateEmail(email)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userReference = usersReference.child(firebaseUser.uid)
                        user.id = firebaseUser.uid
                        userReference.setValue(user)
                    } else {
                        Toast.makeText(context, "User was not updated", Toast.LENGTH_SHORT).show()
                    }
                }
                ?.addOnFailureListener {
                    Toast.makeText(context, "User was not updated", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    private fun formatNickname(nickname: String): String {
        return "$nickname$EMAIL_SUFIX"
    }

    fun uploadImage(changedPhotoFilepath: Uri, context: Context) {
        val filename = "profile_images/profile_image_${Firebase.auth.currentUser!!.uid}"
        val ref = storageReference.child(filename)
        ref.putFile(changedPhotoFilepath).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Toast.makeText(context, "Image wasn't uploaded.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
