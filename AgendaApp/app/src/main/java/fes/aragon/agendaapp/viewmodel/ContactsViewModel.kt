package fes.aragon.agendaapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.repository.database.ContactsRepo
import fes.aragon.agendaapp.repository.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ContactsViewModel(private val repo: ContactsRepo) : ViewModel() {

    fun fetchLatestContacts(uid: String) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        kotlin.runCatching {
            repo.getAllContacts(uid)
        }.onSuccess { flow ->
            flow.collect{
                emit(it)
            }
        }.onFailure {
            emit(Resource.Failure(Exception(it.message)))
        }
    }

    fun addContact(uid: String, contactUI: ContactUI, uri: Uri) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.addContact(uid,contactUI,uri)))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun deleteContact(uid: String, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteContact(uid, id)
        }
    }
}

class ContactsViewModelFactory(private val repo: ContactsRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ContactsRepo::class.java).newInstance(repo)
    }
}