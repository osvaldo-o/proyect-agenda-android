package fes.aragon.agendaapp.domain

import fes.aragon.agendaapp.data.model.Contact

interface ContactsRepo {
    suspend fun getAllContacts(uid: String) : Resource<List<Contact>>
}