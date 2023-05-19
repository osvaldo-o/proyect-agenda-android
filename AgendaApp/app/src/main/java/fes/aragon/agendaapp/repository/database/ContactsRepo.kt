package fes.aragon.agendaapp.repository.database

import android.net.Uri
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.repository.Resource
import kotlinx.coroutines.flow.Flow

interface ContactsRepo {
    suspend fun getAllContacts(): Flow<Resource<List<ContactUI>>>
    suspend fun addContact(contactUI: ContactUI, image:  ByteArray)
    suspend fun deleteContact(contactUI: ContactUI)
    suspend fun updateContact(contactUI: ContactUI, image:  ByteArray?)
}