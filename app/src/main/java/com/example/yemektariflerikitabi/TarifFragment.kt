package com.example.yemektariflerikitabi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.yemektariflerikitabi.databinding.FragmentTarifBinding
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


// TODO: Rename parameter arguments, choose names that match

class TarifFragment : Fragment() {

    private lateinit var binding: FragmentTarifBinding

    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTarifBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.kaydetButtonId.setOnClickListener {
            kaydet(it)
        }

        binding.imageView.setOnClickListener {
            gorselSec(it)
        }

        arguments?.let{
            var gelenBilgi = TarifFragmentArgs.fromBundle(it).bilgi

            if(gelenBilgi.equals("menudenGeldim")){
                //yeni yemek eklemeye geldi
                binding.yemekIsmiTextId.setText("")
                binding.yemekMalzemeTextId.setText("")
                binding.kaydetButtonId.visibility = View.VISIBLE

                val gorselSecmeArkaplan?? = BitmapFactory.decodeResource(context?.resources, R.drawable.gorselsecimi)
                binding.imageView.setImageBitmap(gorselSecmeArkaplan??)

            }else{
                //var olan yeme??i g??r??nt??lemeye geldi
                binding.kaydetButtonId.visibility = View.INVISIBLE
                val secilenId = TarifFragmentArgs.fromBundle(it).id

                context?.let {
                    try {
                        val database = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                        val cursor = database.rawQuery("SELECT * FROM yemekler WHERE id = ?", arrayOf(secilenId.toString()))
                        val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                        val yemekIdIndex = cursor.getColumnIndex(("id"))
                        val yemekGorseli = cursor.getColumnIndex("gorsel")

                        while (cursor.moveToNext()){
                            binding.yemekIsmiTextId.setText(cursor.getString(yemekIsmiIndex))
                            binding.yemekMalzemeTextId.setText(cursor.getString(yemekIdIndex))

                            val byteDizisi = cursor.getBlob(yemekGorseli)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)

                            binding.imageView.setImageBitmap(bitmap)
                        }
                        cursor.close()
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }

            }
        }


    }


    private fun kaydet(view: View) {
        //SQLite'a kaydetme
        val yemekIsmi = binding.yemekIsmiTextId.text.toString()
        val yemekMalzemeleri = binding.yemekMalzemeTextId.text.toString()

        if(secilenBitmap != null){
            val kucukBitmap = bitmapBoyutDusur(secilenBitmap!!, 300)

            //g??rseller sql'de veya cloudda jpeg, png olarak tutulmaz veri olarak tutulur
            //bu y??zden g??rselimizi veriye d??n????t??r??yoruz
            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteDizisi = outputStream.toByteArray()


            try {
                context?.let {
                    val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler(id INTEGER PRIMARY KEY, yemekismi VARCHAR, yemekmalzemesi VARCHAR, gorsel BLOB)")

                    val sqlString = "INSERT INTO yemekler (yemekismi,yemekmalzemesi,gorsel) VALUES(?,?,?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1,yemekIsmi)
                    statement.bindString(2,yemekMalzemeleri)
                    statement.bindBlob(3,byteDizisi)
                    statement.execute()
                }
            }catch (e : Exception){
                e.printStackTrace()
            }

            //kay??t al??nd??ktan sonra tarif listesine geri d??nelim
            val action = TarifFragmentDirections.actionTarifFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action)
        }


    }

    private fun gorselSec(view: View) {

        //izinler activity ??zerinden kontrol edilir
        //activity varsa...
        activity?.let {
            //haf??za okuma izni kontrol et
            if (ContextCompat.checkSelfPermission(
                    it.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                //izin verilmediyse, izin iste
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }else{
                //izin zaten verilmi??se tekrar izin istemeden galeriye git
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //izni ald??k
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            //g??rselin haf??zadaki konumunu ald??k
            secilenGorsel = data.data


            //g??rsel konumundan g??rsele d??n??????m
            try {

                context?.let {
                    if(secilenGorsel != null){
                        if(Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(it.contentResolver,secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                    }
                }

            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //SQLite'da veya cloudda verilerin 1mb'den b??y??k olmamas?? daha iyi
    //bu sebeple ??ekilen g??r??nt??lerin boyutunu d??????r??cez
    private fun bitmapBoyutDusur(kullaniciBitmap: Bitmap, maxBoyut: Int) : Bitmap{

        var width = kullaniciBitmap.width
        var height = kullaniciBitmap.height

        //g??rselin geni??lik-y??kseklik oran??n?? buluyoruz.
        //bu sayede boyutu k??????lt??rken d??zg??n bir ??ekilde k??????lecek
        val bitmapOrani : Double = width.toDouble() / height.toDouble()

        if(bitmapOrani >1 ){
            //g??rselimiz yatay
            width = maxBoyut
            val kisaltilmisHeight = width / bitmapOrani
            height = kisaltilmisHeight.toInt()
        }else{
            //g??rselimiz dikey
            height = maxBoyut
            val kisatlilmisWidth = height * bitmapOrani
            width = kisatlilmisWidth.toInt()
        }


        return Bitmap.createScaledBitmap(kullaniciBitmap,width,height,true)
    }

}