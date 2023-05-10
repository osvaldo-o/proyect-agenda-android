package fes.aragon.agendaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import fes.aragon.agendaapp.repository.Resource
import fes.aragon.agendaapp.repository.auth.AuthRepo
import kotlinx.coroutines.Dispatchers

class AuthViewModel(private val repo: AuthRepo) : ViewModel() {
    fun signIn(email: String, password: String) = liveData(Dispatchers.IO) {
        try {
            emit(Resource.Success(repo.signIn(email, password)))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
    fun signUp(email: String, password: String, name: String) = liveData(Dispatchers.IO) {
        try {
            emit(Resource.Success(repo.signUp(email, password, name)))
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }
}

class AuthViewModelFactory(private val repo: AuthRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AuthRepo::class.java).newInstance(repo)
    }
}