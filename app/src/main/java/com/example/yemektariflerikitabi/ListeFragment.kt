package com.example.yemektariflerikitabi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yemektariflerikitabi.databinding.FragmentListeBinding
import java.lang.Exception


class ListeFragment : Fragment() {

    var yemekIsimListesi = ArrayList<String>()
    var yemekIdListesi = ArrayList<Int>()
    private lateinit var listeAdapter : ListeRecycleAdapter
    private lateinit var binding: FragmentListeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter = ListeRecycleAdapter(yemekIsimListesi,yemekIdListesi)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = listeAdapter

        sqlVeriAlma()
    }

    private fun sqlVeriAlma(){
        try {
            context?.let {
                val database = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)

                val cursor = database.rawQuery("SELECT * FROM yemekler",null)
                val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                val yemekIdIndex = cursor.getColumnIndex(("id"))

                yemekIsimListesi.clear()
                yemekIdListesi.clear()

                while (cursor.moveToNext()){
                    yemekIsimListesi.add(cursor.getString(yemekIsmiIndex))
                    yemekIdListesi.add(cursor.getInt(yemekIdIndex))
                }
                listeAdapter.notifyDataSetChanged()
                cursor.close()
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}