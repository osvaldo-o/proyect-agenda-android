package fes.aragon.agendaapp.domain.database

import fes.aragon.agendaapp.data.model.Contact
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.domain.Resource

class ContactRepoImpl(private val dataSource: ContactDataSource) : ContactsRepo {
    override suspend fun getAllContacts(uid: String): Resource<List<Contact>> = dataSource.getAllContacts(uid)
}