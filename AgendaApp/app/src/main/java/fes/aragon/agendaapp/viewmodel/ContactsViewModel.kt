package fes.aragon.agendaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import fes.aragon.agendaapp.domain.ContactsRepo
import fes.aragon.agendaapp.domain.Resource
import kotlinx.coroutines.Dispatchers

class ContactsViewModel(private val repo: ContactsRepo) : ViewModel() {

    fun fetchLatestContacts(uid: String) = liveData(Dispatchers.IO){
        emit(Resource.Loading())
        try {
            emit(repo.getAllContacts(uid))
        } catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}

class ContactsViewModelFactory(private val repo: ContactsRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ContactsRepo::class.java).newInstance(repo)
    }
}