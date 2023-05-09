package fes.aragon.agendaapp.domain.database

import fes.aragon.agendaapp.data.model.Contact
import fes.aragon.agendaapp.domain.Resource

interface ContactsRepo {
    suspend fun getAllContacts(uid: String) : Resource<List<Contact>>
}