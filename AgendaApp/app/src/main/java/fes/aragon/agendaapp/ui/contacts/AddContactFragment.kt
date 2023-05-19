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
            val email = binding.EditTextEmail.text.toString().trim()
            val name = binding.EditTextName.text.toString().trim()
            val phone = binding.EditTextPhone.text.toString().trim()
            if (validate(name, email, phone)) addContact(name, email, phone)
        }
    }

    private fun isOnline(): Boolean {
        val connMgr = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
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

    private fun validate(name: String, email: String, phone: String): Boolean {
        if (name.isEmpty()) {
            binding.EditTextName.error = "Nombre vacio"
            return false
        }
        if (email.isEmpty()) {
            binding.EditTextEmail.error = "Correo vacio"
            return false
        }
        if (phone.isEmpty()) {
            binding.EditTextPhone.error = "Telefono vacio"
            return false
        }
        if (image == null) {
            Toast.makeText(requireContext(),"Te falta la foto", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!isOnline()) {
            Toast.makeText(requireContext(),"No hay conexión a intenet",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}