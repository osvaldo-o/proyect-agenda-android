package fes.aragon.agendaapp.domain.database

import android.net.Uri
import fes.aragon.agendaapp.data.model.Contact
import fes.aragon.agendaapp.domain.Resource
import kotlinx.coroutines.flow.Flow

interface ContactsRepo {
    suspend fun getAllContacts(uid: String): Flow<Resource<List<Contact>>>
    suspend fun addContact(uid: String, contact: Contact, uri: Uri)
}