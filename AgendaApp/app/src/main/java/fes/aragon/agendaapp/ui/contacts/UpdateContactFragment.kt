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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.databinding.FragmentUpdateContactBinding
import fes.aragon.agendaapp.repository.Resource
import fes.aragon.agendaapp.ui.button.ProgressButton
import fes.aragon.agendaapp.ui.main.Utils
import fes.aragon.agendaapp.viewmodel.ContactsViewModel
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class UpdateContactFragment() : Fragment(R.layout.fragment_update_contact) {
    private lateinit var binding: FragmentUpdateContactBinding
    private lateinit var progressButton: ProgressButton
    private lateinit var contactUI: ContactUI
    private var image: ByteArray? = null
    private val utils = Utils()
    private val viewModel: ContactsViewModel by viewModels()

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

    private fun validate(name: String, email: String, phone: String): Boolean {
        var pass = true
        if (name.isEmpty()) {
            binding.EditTextName.error = getString(R.string.campo_vacio)
            pass = false
        }
        if (email.isEmpty()) {
            binding.EditTextEmail.error = getString(R.string.campo_vacio)
            pass = false
        }
        if (phone.isEmpty()) {
            binding.EditTextPhone.error = getString(R.string.campo_vacio)
            pass = false
        }
        if (!utils.isOnline(requireContext())) {
            Toast.makeText(requireContext(),R.string.sin_internet,Toast.LENGTH_SHORT).show()
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
        progressButton = ProgressButton(binding.buttonUpdate.root,getString(R.string.boton_actualizar))
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
                    progressButton.buttonActivate(getString(R.string.subiendo_cambios))
                }
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_updateContactFragment_to_contactsFragment)
                }
                is Resource.Failure -> {
                    progressButton.buttonFinish(getString(R.string.boton_actualizar))
                    Toast.makeText(requireContext(),it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}