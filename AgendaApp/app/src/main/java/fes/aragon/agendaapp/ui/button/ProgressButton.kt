package fes.aragon.agendaapp.ui.button

import android.view.View
import fes.aragon.agendaapp.databinding.ProgressBtnLayoutBinding

class ProgressButton(view: View, textInit: String) {
    private var binding: ProgressBtnLayoutBinding

    init {
        binding = ProgressBtnLayoutBinding.bind(view)
        binding.textView3.text = textInit
    }

    fun buttonActivate(text: String) {
        binding.progressBar3.visibility = View.VISIBLE
        binding.cardView.isClickable = false
        binding.textView3.text = text
    }

    fun buttonFinish(text: String) {
        binding.progressBar3.visibility = View.GONE
        binding.cardView.isClickable = true
        binding.textView3.text = text
    }
}