package com.example.yemektariflerikitabi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.yemek_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.yemekEkleItem){
            val action = ListeFragmentDirections.actionListeFragmentToTarifFragment()

            //activity_main.xml de bulunan navHostFragment'ın id si veriliyor
            Navigation.findNavController(this, R.id.fragmentContainerView).navigate(action)
        }

        return super.onOptionsItemSelected(item)
    }

}