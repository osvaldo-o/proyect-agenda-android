package fes.aragon.agendaapp.repository.database

import android.net.Uri
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.repository.Resource
import kotlinx.coroutines.flow.Flow

class ContactRepoImpl(private val dataSource: ContactDataSource) : ContactsRepo {
    override suspend fun getAllContacts(uid: String): Flow<Resource<List<ContactUI>>> = dataSource.getAllContacts(uid)
    override suspend fun addContact(uid: String, contactUI: ContactUI, uri: Uri) = dataSource.addContact(uid,contactUI,uri)
    override suspend fun deleteContact(uid: String, contactUI: ContactUI) = dataSource.deleteContact(uid, contactUI)
    override suspend fun updateContact(uid: String, contactUI: ContactUI, uri: Uri?) = dataSource.updateContact(uid, contactUI, uri)
}