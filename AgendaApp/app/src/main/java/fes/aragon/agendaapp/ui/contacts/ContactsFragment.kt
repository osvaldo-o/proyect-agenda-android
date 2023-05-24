package fes.aragon.agendaapp.ui.contacts

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.databinding.FragmentContactsBinding
import fes.aragon.agendaapp.repository.Resource
import fes.aragon.agendaapp.ui.contacts.adapters.ContactAdapter
import fes.aragon.agendaapp.ui.contacts.adapters.OnClickListener
import fes.aragon.agendaapp.viewmodel.ContactsViewModel

@AndroidEntryPoint
class ContactsFragment : Fragment(R.layout.fragment_contacts), OnClickListener {
    private var contactSelect: ContactUI? = null
    private lateinit var binding: FragmentContactsBinding
    private val viewModel: ContactsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentContactsBinding.bind(view)

        getAllContacts()
        binding.addContact.setOnClickListener {
            findNavController().navigate(R.id.action_contactsFragment_to_addContactFragment)
        }

        binding.close.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_contactsFragment_to_homeFragment)
        }

        binding.update.setOnClickListener {
            updateContact()
        }

        binding.delete.setOnClickListener {
            if (isOnline()) { deleteContact() }
            else{ Toast.makeText(requireContext(),"No hay conexión a intenet",Toast.LENGTH_SHORT).show() }
        }
    }

    private fun getAllContacts() {
        viewModel.fetchLatestContacts().observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.adapter = ContactAdapter(it.data,this)
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(),"${it.exception}",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateContact() {
        if (contactSelect != null) {
            findNavController().navigate(R.id.action_contactsFragment_to_updateContactFragment,
                bundleOf("id" to contactSelect!!.id, "email" to contactSelect!!.email, "name" to contactSelect!!.name, "picture" to contactSelect!!.picture, "phone" to contactSelect!!.phone, "uuid_picture" to contactSelect!!.uuid_picture))
        } else {
            Toast.makeText(requireContext(),R.string.not_select_contact,Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteContact() {
        if (contactSelect != null){
            viewModel.deleteContact(contactSelect!!).observe(viewLifecycleOwner, Observer {
                when(it){
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Toast.makeText(requireContext(),"Contact Eliminado",Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(),"${it.exception}",Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            Toast.makeText(requireContext(),R.string.not_select_contact,Toast.LENGTH_SHORT).show()
        }
    }

    private fun isOnline(): Boolean {
        val connMgr = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    override fun onClick(contactUI: ContactUI) {
        contactSelect = contactUI.copy()
    }
}