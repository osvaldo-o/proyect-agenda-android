package fes.aragon.agendaapp.ui.contacts

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.databinding.FragmentAddContactBinding
import fes.aragon.agendaapp.repository.Resource
import fes.aragon.agendaapp.repository.database.ContactRepoImpl
import fes.aragon.agendaapp.ui.button.ProgressButton
import fes.aragon.agendaapp.viewmodel.ContactsViewModel
import fes.aragon.agendaapp.viewmodel.ContactsViewModelFactory
import java.io.ByteArrayOutputStream

class AddContactFragment : Fragment(R.layout.fragment_add_contact) {
    private lateinit var binding: FragmentAddContactBinding
    private lateinit var progressButton: ProgressButton
    private var image: ByteArray? = null
    private val viewModel by viewModels<ContactsViewModel> { ContactsViewModelFactory(ContactRepoImpl(ContactDataSource())) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddContactBinding.bind(view)
        progressButton = ProgressButton(binding.buttonAddContact.root,"AGREGAR")

        binding.buttonAddPicture.setOnClickListener {
            startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        binding.buttonAddContact.cardView.setOnClickListener {
            addContact()
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

    private fun addContact () {
        viewModel.addContact(ContactUI(email = binding.EditTextEmail.text.toString(),name = binding.EditTextName.text.toString(), phone = binding.EditTextPhone.text.toString()),image!!).observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    progressButton.buttonActivate("SUBIENDO CONTACTO")
                }
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_addContactFragment_to_contactsFragment)
                }
                is Resource.Failure -> {
                    progressButton.buttonFinish("AGREGAR")
                    Toast.makeText(requireContext(),it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}