package fes.aragon.agendaapp.data.model

data class ContactUI(var email: String = "", var name: String = "", var picture: String = "", var phone: String = "")

fun ContactUI.toContactUI(id : String): ContactUI = ContactUI(id,this.email,this.name,this.picture,this.phone)
