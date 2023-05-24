package fes.aragon.agendaapp.repository.database

import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.repository.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContactRepoImpl @Inject constructor(private val dataSource: ContactDataSource) : ContactsRepo {
    override suspend fun getAllContacts(): Flow<Resource<List<ContactUI>>> = dataSource.getAllContacts()
    override suspend fun addContact(contactUI: ContactUI, image:  ByteArray) = dataSource.addContact(contactUI,image)
    override suspend fun deleteContact(contactUI: ContactUI) = dataSource.deleteContact(contactUI)
    override suspend fun updateContact(contactUI: ContactUI, image:  ByteArray?) = dataSource.updateContact(contactUI, image)
}