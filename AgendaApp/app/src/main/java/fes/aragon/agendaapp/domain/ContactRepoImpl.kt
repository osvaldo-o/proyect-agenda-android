package fes.aragon.agendaapp.domain

import fes.aragon.agendaapp.data.model.Contact
import fes.aragon.agendaapp.data.remote.ContactDataSource

class ContactRepoImpl(private val dataSource: ContactDataSource) : ContactsRepo {
    override suspend fun getAllContacts(uid: String): Resource<List<Contact>> = dataSource.getAllContacts(uid)
}