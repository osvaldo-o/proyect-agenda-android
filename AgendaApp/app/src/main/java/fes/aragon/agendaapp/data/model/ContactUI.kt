package fes.aragon.agendaapp.data.model

data class ContactUI(val id: String = "", var email: String = "", var name: String = "", var picture: String = "", var phone: String = "", var uuid_picture: String = "")

fun ContactDB.toContactUI(id : String): ContactUI = ContactUI(id = id,email = this.email,name = this.name,picture = this.picture,phone = this.phone, uuid_picture = this.uuid_picture)

fun ContactUI.toContactDB(): ContactDB = ContactDB(this.email,this.name,this.picture,this.phone,this.uuid_picture)