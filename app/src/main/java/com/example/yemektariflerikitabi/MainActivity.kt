package com.example.yemektariflerikitabi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import com.example.yemektariflerikitabi.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.yemek_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.yemekEkleItem){
            val action = ListeFragmentDirections.actionListeFragmentToTarifFragment("menudenGeldim", 0)

            //activity_main.xml de bulunan navHostFragment'ın id si veriliyor
            Navigation.findNavController(this, R.id.fragmentContainerView).navigate(action)
        }

        return super.onOptionsItemSelected(item)
    }

}