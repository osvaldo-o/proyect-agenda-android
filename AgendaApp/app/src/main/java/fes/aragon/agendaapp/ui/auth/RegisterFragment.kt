package fes.aragon.agendaapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.remote.AuthDataSource
import fes.aragon.agendaapp.databinding.FragmentRegisterBinding
import fes.aragon.agendaapp.domain.Resource
import fes.aragon.agendaapp.domain.auth.AuthRepoImpl
import fes.aragon.agendaapp.viewmodel.AuthViewModel
import fes.aragon.agendaapp.viewmodel.AuthViewModelFactory

class RegisterFragment : Fragment(R.layout.fragment_register,) {
    private lateinit var  binding: FragmentRegisterBinding
    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(AuthRepoImpl(AuthDataSource())) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        signUp()
    }

    private fun signUp() {
        binding.buttonToRegister.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            if (validate(name, email, password)){
                viewModel.signUp(email, password,name).observe(viewLifecycleOwner, Observer {
                    when(it){
                        is Resource.Failure -> {
                            Toast.makeText(requireContext(),"${it.exception}", Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            findNavController().navigate(R.id.action_registerFragment2_to_contactsFragment)
                            Toast.makeText(requireContext(),"Bienvenido ${it.data?.email}",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }

    private fun validate(name: String, email: String, password: String): Boolean{
        if (name.isEmpty()){
            binding.editTextName.error = "name vacio"
            return false
        }
        if (email.isEmpty()){
            binding.editTextEmail.error = "email vacio"
            return false
        }
        if (password.isEmpty()){
            binding.editTextPassword.error = "password vacio"
            return false
        }
        return true
    }
}