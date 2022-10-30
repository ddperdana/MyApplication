package com.dedek.myapplication.adapter

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.dedek.myapplication.EditProduk
import com.dedek.myapplication.Helper.MyUtilities
import com.dedek.myapplication.R
import com.dedek.myapplication.model.ProdukModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject


class ProdukAdapter(val context: Context, var listItemMenu: MutableList<ProdukModel.Data>) :
    RecyclerView.Adapter<ProdukAdapter.ViewHolder>() {
      lateinit var progressDialog: ProgressDialog
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(produks: ProdukModel.Data, context: Context) {
            Glide.with(context)
                .load(Uri.parse(produks.image))
                .placeholder(R.drawable.bni)
                .into(itemView.findViewById(R.id.IvItemProduk))
            itemView.findViewById<TextView>(R.id.TvHargaProduk).setText(MyUtilities.numberToCurrency(produks.harga))
            itemView.findViewById<TextView>(R.id.TvNamaProduk).setText(produks.nama_produk)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produks = listItemMenu[position]
        if (produks != null) {
            holder.onBind(produks, context)
            holder.itemView.setOnClickListener {
                val popupMenu = PopupMenu(context,it)
                popupMenu.menuInflater.inflate(R.menu.menu_edit_hapus_produk, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.itemedit -> editProduk(produks)
                        R.id.itemhapus -> hapusProduk(produks)

                    }
                    return@setOnMenuItemClickListener true
                }

                popupMenu.show()

            }


        }


    }

    private fun hapusProduk(produks: ProdukModel.Data) {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Proses")
        progressDialog.setMessage(" Loading... MOhon tunggu !! ")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = true

        //progressDialog.show()

        MaterialAlertDialogBuilder(context).setTitle("Hapus")
            .setMessage("Apakah anda yakin ingin menghapusnya ??")
            .setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
//                Toast.makeText(context, "prose hapus berjalan", Toast.LENGTH_SHORT).show()
                progressDialog.show()
                AndroidNetworking.post("http://belipulsabeta.purja.web.id/public/api/hapusproduk")
                    .addHeaders("Authorization","Bearer01USgSmbrZo6UdFd")
                    .addBodyParameter("id",produks.id.toString())
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener{
                        override fun onResponse(p0: JSONObject?) {
                           // Log.d("", p0.toString())
                            progressDialog.dismiss()
                            val apiStatus = p0?.getInt("api_status")
                            val apiMessage = p0?.getString("api_message")
                            if (apiStatus != null) {
                                if(apiStatus.equals(1)){
                                    notifyDataSetChanged()
                                    Toast.makeText(context, "hapus produk  ${produks.nama_produk}  $apiMessage", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onError(p0: ANError?) {
                          progressDialog.dismiss()
                            Toast.makeText(context, " Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show()
                        }

                    })

            })
            .setNegativeButton("Close", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            .show()

    }

    private fun editProduk(produks: ProdukModel.Data) {

    val intent = Intent(context, EditProduk::class.java)
        intent.putExtra("id",produks.id)
        intent.putExtra("harga",produks.harga)
        intent.putExtra("image", produks.image)
        intent.putExtra("nama_produk",produks.nama_produk)
        context.startActivity(intent)

    }

    override fun getItemCount(): Int = listItemMenu.size
}