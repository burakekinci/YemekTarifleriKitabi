package com.example.yemektariflerikitabi

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.yemektariflerikitabi.databinding.ItemCardBinding

class ListeRecycleAdapter(val yemekListesi : ArrayList<String>, val idListesi : ArrayList<Int>) :
    RecyclerView.Adapter<ListeRecycleAdapter.ItemCardDesign>() {

    class ItemCardDesign(val itemCardBinding: ItemCardBinding) : RecyclerView.ViewHolder(itemCardBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCardDesign {
        val inflater = LayoutInflater.from(parent.context)
        val itemCardBinding = ItemCardBinding.inflate(inflater,parent,false)
        return ItemCardDesign(itemCardBinding)
    }

    override fun onBindViewHolder(holder: ItemCardDesign, position: Int) {
        holder.itemCardBinding.itemCardId.text = yemekListesi[position]
        holder.itemCardBinding.root.setOnClickListener {
            val action = ListeFragmentDirections.actionListeFragmentToTarifFragment("recyclerdanGeldim", idListesi[position])
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return yemekListesi.size
    }


}