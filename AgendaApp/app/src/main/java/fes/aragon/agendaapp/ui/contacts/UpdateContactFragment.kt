package fes.aragon.agendaapp.ui.contacts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import fes.aragon.agendaapp.BuildConfig
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.databinding.FragmentUpdateContactBinding
import fes.aragon.agendaapp.repository.database.ContactRepoImpl
import fes.aragon.agendaapp.viewmodel.ContactsViewModel
import fes.aragon.agendaapp.viewmodel.ContactsViewModelFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UpdateContactFragment() : Fragment(R.layout.fragment_update_contact) {
    private lateinit var binding: FragmentUpdateContactBinding
    private lateinit var contactUI: ContactUI
    private val uid = FirebaseAuth.getInstance().uid ?: ""
    private lateinit var currentPhotoPath: String
    private var photoURI: Uri? = null
    private val viewModel by viewModels<ContactsViewModel> { ContactsViewModelFactory(
        ContactRepoImpl(ContactDataSource())
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUpdateContactBinding.bind(view)
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
            .into(binding.imageView)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                1000
            )
        }else{
            dispatchTakePictureIntent()
        }

        binding.buttonUpdate.setOnClickListener {
            viewModel.updateContact(uid, ContactUI(contactUI.id,binding.EditTextEmail.text.toString(),binding.EditTextName.text.toString(),contactUI.picture,binding.EditTextPhone.text.toString(),contactUI.uuid_picture),photoURI)
            findNavController().navigate(R.id.action_updateContactFragment_to_contactsFragment)
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm-ss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            activity?.let {
                takePictureIntent.resolveActivity(it.packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        null
                    }
                    photoFile?.also {file ->
                        photoURI = FileProvider.getUriForFile(
                            Objects.requireNonNull(requireContext()),
                            BuildConfig.APPLICATION_ID+".provider",file)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        val data = registerForActivityResult(ActivityResultContracts.TakePicture()) {
                            binding.imageView.setImageURI(photoURI)
                        }
                        binding.buttonUpdatePicture.setOnClickListener {
                            data.launch(photoURI)
                        }
                    }
                }
            }
        }
    }

}