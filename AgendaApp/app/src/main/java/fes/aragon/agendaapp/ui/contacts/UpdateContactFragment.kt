package fes.aragon.agendaapp.ui.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.databinding.FragmentUpdateContactBinding
import fes.aragon.agendaapp.repository.database.ContactRepoImpl
import fes.aragon.agendaapp.viewmodel.ContactsViewModel
import fes.aragon.agendaapp.viewmodel.ContactsViewModelFactory

class UpdateContactFragment() : Fragment(R.layout.fragment_update_contact) {
    private lateinit var binding: FragmentUpdateContactBinding
    private lateinit var contactUI: ContactUI
    private val uid = FirebaseAuth.getInstance().uid ?: ""
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
            contactUI = ContactUI(id, email, name, picture, phone)
        }

        binding.EditTextEmail.setText(contactUI.email)
        binding.EditTextName.setText(contactUI.name)
        binding.EditTextPhone.setText(contactUI.phone)
        Glide.with(requireContext())
            .load(contactUI.picture)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.imageView)

        binding.buttonUpdate.setOnClickListener {
            viewModel.updateContact(uid, ContactUI(contactUI.id,binding.EditTextEmail.text.toString(),binding.EditTextName.text.toString(),contactUI.picture,binding.EditTextPhone.text.toString()))
            findNavController().navigate(R.id.action_updateContactFragment_to_contactsFragment)
        }
    }
}