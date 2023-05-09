package fes.aragon.agendaapp.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import fes.aragon.agendaapp.data.model.Contact
import fes.aragon.agendaapp.domain.Resource
import kotlinx.coroutines.tasks.await

class ContactDataSource {
    suspend fun getAllContacts(uid: String) : Resource<List<Contact>> {
        val contactsList = mutableListOf<Contact>()
        val querySnapshot = FirebaseFirestore.getInstance().collection("usuarios").document(uid)
            .collection("contactos").get().await()
        querySnapshot.map {
            it.toObject(Contact::class.java)?.let {
                contactsList.add(it)
            }
        }
        return Resource.Success(contactsList)
    }
}