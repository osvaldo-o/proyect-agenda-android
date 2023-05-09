package fes.aragon.agendaapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.remote.AuthDataSource
import fes.aragon.agendaapp.databinding.FragmentLoginBinding
import fes.aragon.agendaapp.domain.Resource
import fes.aragon.agendaapp.domain.auth.AuthRepoImpl
import fes.aragon.agendaapp.viewmodel.AuthViewModel
import fes.aragon.agendaapp.viewmodel.AuthViewModelFactory

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(AuthRepoImpl(
        AuthDataSource()
    )) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        isUserLoggedIn()
        doLogin()
        goToSignUp()
    }

    private fun isUserLoggedIn() {
        if (firebaseAuth.currentUser != null){
            findNavController().navigate(R.id.action_loginFragment_to_contactsFragment)
        }
    }

    private fun doLogin() {
        binding.buttonSingIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            if(validateCredentials(email, password)){
                signIn(email, password)
            }
        }
    }

    private fun goToSignUp() {
        binding.buttonToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment2)
        }
    }

    private fun validateCredentials(email: String, password: String): Boolean{
        if (email.isEmpty()){
            binding.editTextEmail.error = "Email vacio"
            return false
        }
        if (password.isEmpty()){
            binding.editTextPassword.error = "Password vacio"
            return false
        }
        return true
    }

    private fun signIn(email: String,password: String){
        viewModel.signIn(email, password).observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_contactsFragment)
                    Toast.makeText(requireContext(),"Bienvenido ${it.data?.email}",Toast.LENGTH_SHORT).show()
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(),"${it.exception}",Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {}
            }
        })
    }
}