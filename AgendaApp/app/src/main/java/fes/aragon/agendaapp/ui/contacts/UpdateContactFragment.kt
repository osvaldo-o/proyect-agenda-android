package fes.aragon.agendaapp.ui.contacts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.databinding.FragmentUpdateContactBinding
import fes.aragon.agendaapp.repository.Resource
import fes.aragon.agendaapp.repository.database.ContactRepoImpl
import fes.aragon.agendaapp.ui.button.ProgressButton
import fes.aragon.agendaapp.viewmodel.ContactsViewModel
import fes.aragon.agendaapp.viewmodel.ContactsViewModelFactory
import java.io.ByteArrayOutputStream
import java.util.*

class UpdateContactFragment() : Fragment(R.layout.fragment_update_contact) {
    private lateinit var binding: FragmentUpdateContactBinding
    private lateinit var progressButton: ProgressButton
    private lateinit var contactUI: ContactUI
    private var image: ByteArray? = null
    private val viewModel by viewModels<ContactsViewModel> { ContactsViewModelFactory(
        ContactRepoImpl(ContactDataSource())
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUpdateContactBinding.bind(view)
        init()

        binding.buttonUpdatePicture.setOnClickListener {
            startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        binding.buttonUpdate.cardView.setOnClickListener {
            val email = binding.EditTextEmail.text.toString().trim()
            val name = binding.EditTextName.text.toString().trim()
            val phone = binding.EditTextPhone.text.toString().trim()
            if (validate(name, email, phone)) updateContact(name, email, phone)
        }
    }

    private fun isOnline(): Boolean {
        val connMgr = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    private fun validate(name: String, email: String, phone: String): Boolean {
        var pass = true
        if (name.isEmpty()) {
            binding.EditTextName.error = "Nombre vacio"
            pass = false
        }
        if (email.isEmpty()) {
            binding.EditTextEmail.error = "Correo vacio"
            pass = false
        }
        if (phone.isEmpty()) {
            binding.EditTextPhone.error = "Telefono vacio"
            pass = false
        }
        if (!isOnline()) {
            Toast.makeText(requireContext(),"No hay conexión a intenet",Toast.LENGTH_SHORT).show()
            pass = false
        }
        return pass
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

    private fun init() {
        progressButton = ProgressButton(binding.buttonUpdate.root,"ACTUALIZAR")
        arguments?.let {
            val id = it.getString("id")!!
            val email = it.getString("email")!!
            val name = it.getString("name")!!
            val picture = it.getString("picture")!!
            val phone = it.getString("phone")!!
            val uuid = it.getString("uuid_picture")!!
            contactUI = ContactUI(id, email, name, picture, phone, uuid)
        }

        binding.EditTextEmail.setText(contactUI.email)
        binding.EditTextName.setText(contactUI.name)
        binding.EditTextPhone.setText(contactUI.phone)
        Glide.with(requireContext())
            .load(contactUI.picture)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.imageView)
    }

    private fun updateContact(name: String, email: String, phone: String) {
        viewModel.updateContact(ContactUI(contactUI.id,email,name,contactUI.picture,phone,contactUI.uuid_picture),image).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it){
                is Resource.Loading -> {
                    progressButton.buttonActivate("SUBIENDO CAMBIOS")
                }
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_updateContactFragment_to_contactsFragment)
                }
                is Resource.Failure -> {
                    progressButton.buttonFinish("ACTUALIZAR")
                    Toast.makeText(requireContext(),it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}