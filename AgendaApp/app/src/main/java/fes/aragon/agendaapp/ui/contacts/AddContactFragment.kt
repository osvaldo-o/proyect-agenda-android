package fes.aragon.agendaapp.ui.contacts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.databinding.FragmentAddContactBinding
import fes.aragon.agendaapp.repository.Resource
import fes.aragon.agendaapp.ui.button.ProgressButton
import fes.aragon.agendaapp.ui.main.Utils
import fes.aragon.agendaapp.viewmodel.ContactsViewModel
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class AddContactFragment : Fragment(R.layout.fragment_add_contact) {
    private lateinit var binding: FragmentAddContactBinding
    private lateinit var progressButton: ProgressButton
    private var image: ByteArray? = null
    private val utils = Utils()
    private val viewModel: ContactsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddContactBinding.bind(view)
        progressButton = ProgressButton(binding.buttonAddContact.root,getString(R.string.boton_agregar))

        binding.buttonAddPicture.setOnClickListener {
            startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
        
        binding.buttonAddContact.cardView.setOnClickListener {
            val email = binding.EditTextEmail.text.toString().trim()
            val name = binding.EditTextName.text.toString().trim()
            val phone = binding.EditTextPhone.text.toString().trim()
            if (validate(name, email, phone)) addContact(name, email, phone)
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
        if(it.resultCode == Activity.RESULT_OK){
            val imageBitmap = it.data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(imageBitmap)
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
            image = baos.toByteArray()
        }
    })

    private fun addContact (name: String, email: String, phone: String) {
        viewModel.addContact(ContactUI(email = email,name = name, phone = phone),image!!).observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    progressButton.buttonActivate(getString(R.string.subiendo_contacto))
                }
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_addContactFragment_to_contactsFragment)
                }
                is Resource.Failure -> {
                    progressButton.buttonFinish(getString(R.string.boton_agregar))
                    Toast.makeText(requireContext(),it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validate(name: String, email: String, phone: String): Boolean {
        if (name.isEmpty()) {
            binding.EditTextName.error = getString(R.string.campo_vacio)
            return false
        }
        if (email.isEmpty()) {
            binding.EditTextEmail.error = getString(R.string.campo_vacio)
            return false
        }
        if (phone.isEmpty()) {
            binding.EditTextPhone.error = getString(R.string.campo_vacio)
            return false
        }
        if (image == null) {
            utils.alert(requireContext(),getString(R.string.sin_foto))
            return false
        }
        if (!utils.isOnline(requireContext())) {
            Toast.makeText(requireContext(),R.string.sin_internet,Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}