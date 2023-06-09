package fes.aragon.agendaapp.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import fes.aragon.agendaapp.data.model.ContactDB
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.data.model.toContactDB
import fes.aragon.agendaapp.data.model.toContactUI
import fes.aragon.agendaapp.repository.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ContactDataSourceImpl @Inject constructor() : ContactDataSource {

    private val uid = FirebaseAuth.getInstance().uid ?: ""
    override suspend fun getAllContacts() : Flow<Resource<List<ContactUI>>> = callbackFlow {
        val contactsList = mutableListOf<ContactUI>()

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
                    contactsList.add(it.toObject(ContactDB::class.java).toContactUI(it.id))
                }
            }catch (e: Throwable){
                close(e)
            }
            trySend(Resource.Success(contactsList)).isSuccess
        }

        awaitClose{ subscribe?.remove() }
    }

    override suspend fun addContact(contactUI: ContactUI, image:  ByteArray) {
        contactUI.uuid_picture = UUID.randomUUID().toString()
        contactUI.picture = FirebaseStorage.getInstance().reference.child("images/$uid/${contactUI.uuid_picture}").putBytes(image).await().storage.downloadUrl.await().toString()
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("contacts").add(contactUI.toContactDB()).await()
    }

    override suspend fun deleteContact(contactUI: ContactUI) {
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .collection("contacts").document(contactUI.id).delete().await()
        FirebaseStorage.getInstance().reference.child("images/$uid/${contactUI.uuid_picture}").delete().await()
    }

    override suspend fun updateContact(contactUI: ContactUI, image:  ByteArray?) {
        if (image != null){
            contactUI.picture = FirebaseStorage.getInstance().reference.child("images/$uid/${contactUI.uuid_picture}").putBytes(image).await().storage.downloadUrl.await().toString()
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("contacts").document(contactUI.id).set(contactUI.toContactDB()).await()
        }else{
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("contacts").document(contactUI.id).set(contactUI.toContactDB()).await()
        }
    }
}