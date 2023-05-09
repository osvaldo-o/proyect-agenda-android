package fes.aragon.agendaapp.domain.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface AuthRepo {
    suspend fun signIn(email: String,password: String): FirebaseUser?
}