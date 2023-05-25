package fes.aragon.agendaapp.repository.auth

import com.google.firebase.auth.FirebaseUser
import fes.aragon.agendaapp.data.remote.AuthDataSourceImpl
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(private val dataSource: AuthDataSourceImpl) : AuthRepo {
    override suspend fun signIn(email: String, password: String): FirebaseUser? = dataSource.signIn(email, password)
    override suspend fun signUp(email: String, password: String, name: String): FirebaseUser? = dataSource.signUp(email, password,name)
}