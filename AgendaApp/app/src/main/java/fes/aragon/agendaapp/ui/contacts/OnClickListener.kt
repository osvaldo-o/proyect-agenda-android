package fes.aragon.agendaapp.ui.contacts

import fes.aragon.agendaapp.data.model.Contact

interface OnClickListener {
    fun onClick(contact: Contact)
}