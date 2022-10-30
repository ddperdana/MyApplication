package com.dedek.myapplication

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.dedek.myapplication.adapter.ProdukAdapter
import com.dedek.myapplication.model.ProdukModel
import com.google.gson.Gson
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var progressDialog: ProgressDialog
    lateinit var recyclerView: RecyclerView
    lateinit var btninputProduk : Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Proses")
        progressDialog.setMessage("Loading .. Mohon Tunggu")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = true

        recyclerView = findViewById(R.id.RvProduk)

        btninputProduk = findViewById(R.id.btnToInput)

        btninputProduk.setOnClickListener {
            val intent = Intent(this,InputProduk::class.java)
            startActivity(intent)
        }

        val status = true
        if (status){
            LoadDataProduk("active")

        }





    }

    private fun LoadDataProduk(status: String) {
        progressDialog.show()
        AndroidNetworking.post("http://belipulsabeta.purja.web.id/public/api/produk")
            .addHeaders("Authorization","Bearer01USgSmbrZo6UdFd")
            .addBodyParameter("status",status)
            .addBodyParameter("id","1")
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsJSONObject(object  : JSONObjectRequestListener{
                override fun onResponse(p0: JSONObject?) {
                    progressDialog.dismiss()
                    Log.d("produk", p0.toString())
                    val apiStatus = p0?.getInt("api_status")
                    val apiMessage = p0?.getString("api_message")

                    if(apiStatus!!.equals(1)) {
                        val data = Gson().fromJson(p0.toString(), ProdukModel::class.java).data

                        if (data != null) {

                            val produkAdapter = ProdukAdapter(this@MainActivity,
                                data as MutableList<ProdukModel.Data>
                            )
                            recyclerView.apply {
                               adapter = produkAdapter
                                layoutManager = GridLayoutManager(this@MainActivity, 2)
                                setHasFixedSize(true)

                            }

                        }

                    } else {
                        Toast.makeText(this@MainActivity, apiMessage, Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onError(p0: ANError?) {
                    progressDialog.dismiss()
                    Toast.makeText(this@MainActivity, "load data error", Toast.LENGTH_SHORT).show()
                }

            })


    }
}