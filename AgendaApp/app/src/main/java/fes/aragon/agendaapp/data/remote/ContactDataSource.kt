package fes.aragon.agendaapp.data.remote

import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.repository.Resource
import kotlinx.coroutines.flow.Flow

interface ContactDataSource {
    suspend fun getAllContacts() : Flow<Resource<List<ContactUI>>>
    suspend fun addContact(contactUI: ContactUI, image:  ByteArray)
    suspend fun deleteContact(contactUI: ContactUI)
    suspend fun updateContact(contactUI: ContactUI, image:  ByteArray?)
}