package fes.aragon.agendaapp.repository.database

import android.net.Uri
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.repository.Resource
import kotlinx.coroutines.flow.Flow

interface ContactsRepo {
    suspend fun getAllContacts(uid: String): Flow<Resource<List<ContactUI>>>
    suspend fun addContact(uid: String, contactUI: ContactUI, uri: Uri)
    suspend fun deleteContact(uid: String, id: String)
    suspend fun updateContact(uid : String, contactUI: ContactUI, id: String)
}