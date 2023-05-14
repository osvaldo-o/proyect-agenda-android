package fes.aragon.agendaapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        isUserLoggedIn()
        binding.buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
        binding.buttonSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_registerFragment2)
        }
    }

    private fun isUserLoggedIn() {
        if (firebaseAuth.currentUser != null){
            findNavController().navigate(R.id.action_homeFragment_to_contactsFragment)
        }
    }
}