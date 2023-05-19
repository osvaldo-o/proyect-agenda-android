package fes.aragon.agendaapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fes.aragon.agendaapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
    }
}