package fes.aragon.agendaapp.data.remote

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import fes.aragon.agendaapp.data.model.Contact
import fes.aragon.agendaapp.domain.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ContactDataSource {
    suspend fun getAllContacts(uid: String) : Flow<Resource<List<Contact>>> = callbackFlow {
        val contactsList = mutableListOf<Contact>()

        var reference: CollectionReference? = null
        try {
            reference = FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("contacts")
        }catch (e: Throwable){
            close(e)
        }

        val subscribe = reference?.addSnapshotListener { value, _ ->
            if (value == null) return@addSnapshotListener
            try {
                contactsList.clear()
                value.map {
                    contactsList.add(it.toObject(Contact::class.java))
                    println("id_document"+it.id)
                }
            }catch (e: Throwable){
                close(e)
            }
            trySend(Resource.Success(contactsList)).isSuccess
        }

        awaitClose{ subscribe?.remove() }
    }

    suspend fun addContact(uid: String, contact: Contact, uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        contact.picture = imagesRef.putFile(uri).await().storage.downloadUrl.await().toString()
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("contacts").add(contact).await()
    }
}