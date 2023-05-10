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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import fes.aragon.agendaapp.BuildConfig
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.model.Contact
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.databinding.FragmentAddContactBinding
import fes.aragon.agendaapp.domain.Resource
import fes.aragon.agendaapp.domain.database.ContactRepoImpl
import fes.aragon.agendaapp.viewmodel.ContactsViewModel
import fes.aragon.agendaapp.viewmodel.ContactsViewModelFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddContactFragment : Fragment(R.layout.fragment_add_contact) {
    private lateinit var binding: FragmentAddContactBinding
    private lateinit var currentPhotoPath: String
    private val viewModel by viewModels<ContactsViewModel> { ContactsViewModelFactory(ContactRepoImpl(ContactDataSource())) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddContactBinding.bind(view)

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
                        val photoURI: Uri = FileProvider.getUriForFile(Objects.requireNonNull(requireContext()),BuildConfig.APPLICATION_ID+".provider",file)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        val data = registerForActivityResult(ActivityResultContracts.TakePicture()) {
                            binding.imageView.setImageURI(null)
                            binding.imageView.setImageURI(photoURI)
                        }
                        binding.buttonAddPicture.setOnClickListener {
                            data.launch(photoURI)
                        }
                        binding.buttonAddContact.setOnClickListener {
                            FirebaseAuth.getInstance().uid?.let { uid ->
                                addContact(uid,photoURI)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addContact (uid: String, photoURI: Uri) {
        val alertDialog = AlertDialog.Builder(requireContext()).setView(R.layout.msg_loading).create()
        viewModel.addContact(uid, Contact(binding.EditTextEmail.text.toString(),binding.EditTextName.text.toString(),binding.EditTextPhone.text.toString()),photoURI).observe(viewLifecycleOwner) {
            when(it){
                is Resource.Loading -> {
                    alertDialog.show()
                }
                is Resource.Success -> {
                    alertDialog.dismiss()
                    findNavController().navigate(R.id.action_addContactFragment_to_contactsFragment)
                }
                is Resource.Failure -> {
                    alertDialog.show()

                }
            }
        }
    }

}