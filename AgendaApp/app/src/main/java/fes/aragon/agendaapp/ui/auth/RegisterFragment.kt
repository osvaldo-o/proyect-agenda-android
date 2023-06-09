package fes.aragon.agendaapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.databinding.FragmentRegisterBinding
import fes.aragon.agendaapp.repository.Resource
import fes.aragon.agendaapp.viewmodel.AuthViewModel

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register,) {
    private lateinit var  binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

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
                            binding.buttonToRegister.isClickable = true
                            Toast.makeText(requireContext(),"${it.exception}", Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            binding.buttonToRegister.isClickable = false
                        }
                        is Resource.Success -> {
                            findNavController().navigate(R.id.action_registerFragment2_to_contactsFragment)
                            Toast.makeText(requireContext(),"Bienvenido $name",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }

    private fun validate(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()){
            binding.editTextName.error = getString(R.string.campo_vacio)
            return false
        }
        if (email.isEmpty()){
            binding.editTextEmail.error = getString(R.string.campo_vacio)
            return false
        }
        if (password.isEmpty()){
            binding.editTextPassword.error = getString(R.string.campo_vacio)
            return false
        }
        return true
    }
}