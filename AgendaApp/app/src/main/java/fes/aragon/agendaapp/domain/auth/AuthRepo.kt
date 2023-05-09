package fes.aragon.agendaapp.domain.auth

import com.google.firebase.auth.FirebaseUser

interface AuthRepo {
    suspend fun signIn(email: String,password: String): FirebaseUser?
    suspend fun signUp(email: String,password: String,name: String): FirebaseUser?
}