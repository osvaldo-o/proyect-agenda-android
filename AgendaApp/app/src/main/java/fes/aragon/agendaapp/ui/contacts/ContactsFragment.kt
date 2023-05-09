package fes.aragon.agendaapp.ui.contacts

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.model.Contact
import fes.aragon.agendaapp.data.remote.ContactDataSource
import fes.aragon.agendaapp.databinding.FragmentContactsBinding
import fes.aragon.agendaapp.domain.database.ContactRepoImpl
import fes.aragon.agendaapp.domain.Resource
import fes.aragon.agendaapp.viewmodel.ContactsViewModel
import fes.aragon.agendaapp.viewmodel.ContactsViewModelFactory

class ContactsFragment : Fragment(R.layout.fragment_contacts), OnClickListener {

    private lateinit var binding: FragmentContactsBinding
    private val viewModel by viewModels<ContactsViewModel> { ContactsViewModelFactory(ContactRepoImpl(ContactDataSource())) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentContactsBinding.bind(view)

        viewModel.fetchLatestContacts("i39xohhuQtksjc6CPVbB").observe(viewLifecycleOwner, Observer {
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

        binding.close.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_contactsFragment_to_loginFragment2)
        }
    }

    override fun onClick(contact: Contact) {
        Toast.makeText(requireContext(),"${contact.name}",Toast.LENGTH_SHORT).show()
    }
}