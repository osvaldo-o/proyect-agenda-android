package fes.aragon.agendaapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.repository.database.ContactsRepo
import fes.aragon.agendaapp.repository.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class ContactsViewModel @Inject constructor(private val repo: ContactsRepo) : ViewModel() {

    fun fetchLatestContacts() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        kotlin.runCatching {
            repo.getAllContacts()
        }.onSuccess { flow ->
            flow.collect{
                emit(it)
            }
        }.onFailure {
            emit(Resource.Failure(Exception(it.message)))
        }
    }

    fun addContact(contactUI: ContactUI, image:  ByteArray) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.addContact(contactUI,image)))
        }catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun updateContact(contactUI: ContactUI, image:  ByteArray?) = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.updateContact(contactUI, image)))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    fun deleteContact(contactUI: ContactUI) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.deleteContact(contactUI)))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}