package fes.aragon.agendaapp.data.remote

import com.google.firebase.auth.FirebaseUser

interface AuthDataSource {
    suspend fun signIn(email: String, password: String): FirebaseUser?
    suspend fun signUp(email: String, password: String, name: String): FirebaseUser?
}