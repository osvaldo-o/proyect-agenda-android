package fes.aragon.agendaapp.ui.contacts.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import fes.aragon.agendaapp.R
import fes.aragon.agendaapp.data.model.ContactUI
import fes.aragon.agendaapp.databinding.ItemContactBinding

class ContactAdapter(private val contactUIS: List<ContactUI>, private val listener: OnClickListener) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemContactBinding.bind(view)
        fun onClick(contactUI: ContactUI) {
            binding.root.setOnClickListener {
                listener.onClick(contactUI)
            }
        }
    }

    override fun getItemCount(): Int = contactUIS.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_contact,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contactUIS[position]
        with(holder){
            binding.name.text = contact.name
            binding.email.text = contact.email
            binding.phone.text = contact.phone
            Glide.with(context)
                .load(contact.picture)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.picture)
            onClick(contact)
        }
    }
}