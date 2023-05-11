package fes.aragon.agendaapp.data.model

data class ContactUI(val id: String = "", var email: String = "", var name: String = "", var picture: String = "", var phone: String = "")

fun ContactDB.toContactUI(id : String): ContactUI = ContactUI(id,this.email,this.name,this.picture,this.phone)