package fes.aragon.agendaapp.domain.auth

import com.google.firebase.auth.FirebaseUser
import fes.aragon.agendaapp.data.remote.AuthDataSource

class AuthRepoImpl(private val dataSource: AuthDataSource) : AuthRepo {
    override suspend fun signIn(email: String, password: String): FirebaseUser? = dataSource.signIn(email, password)
}