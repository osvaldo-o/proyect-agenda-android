package fes.aragon.agendaapp.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import fes.aragon.agendaapp.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(): AuthDataSource{
    override suspend fun signIn(email: String, password: String): FirebaseUser? {
        val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        return authResult.user
    }
    override suspend fun signUp(email: String, password: String, name: String): FirebaseUser? {
        val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
        authResult.user?.uid?.let {
            FirebaseFirestore.getInstance().collection("users").document(it).set(User(email,name)).await()
        }
        return authResult.user
    }
}
