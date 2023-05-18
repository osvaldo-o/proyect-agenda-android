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
import fes.aragon.agendaapp.repository.Resource
import fes.aragon.agendaapp.repository.auth.AuthRepoImpl
import fes.aragon.agendaapp.ui.button.ProgressButton
import fes.aragon.agendaapp.viewmodel.AuthViewModel
import fes.aragon.agendaapp.viewmodel.AuthViewModelFactory

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(AuthRepoImpl(
        AuthDataSource()
    )) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        doLogin()
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
                    binding.buttonSingIn.isClickable = true
                    findNavController().navigate(R.id.action_loginFragment_to_contactsFragment)
                }
                is Resource.Failure -> {
                    binding.buttonSingIn.isClickable = true
                    Toast.makeText(requireContext(),"${it.exception}",Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    binding.buttonSingIn.isClickable = false
                }
            }
        })
    }
}