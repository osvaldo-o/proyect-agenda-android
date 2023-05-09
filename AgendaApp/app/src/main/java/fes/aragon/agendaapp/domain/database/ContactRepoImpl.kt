package fes.aragon.agendaapp.domain.database

import fes.aragon.agendaapp.data.model.Contact
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.domain.Resource
import kotlinx.coroutines.flow.Flow

class ContactRepoImpl(private val dataSource: ContactDataSource) : ContactsRepo {
    override suspend fun getAllContacts(uid: String): Flow<Resource<List<Contact>>> = dataSource.getAllContacts(uid)
    override suspend fun addContact(uid: String, contact: Contact) {
        dataSource.addContact(uid,contact)
    }
}