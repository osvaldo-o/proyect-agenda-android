package fes.aragon.agendaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import fes.aragon.agendaapp.repository.Resource
import fes.aragon.agendaapp.repository.auth.AuthRepo
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repo: AuthRepo) : ViewModel() {
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