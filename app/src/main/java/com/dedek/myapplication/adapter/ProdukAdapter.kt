package com.dedek.myapplication.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dedek.myapplication.R
import com.dedek.myapplication.model.ProdukModel

class ProdukAdapter(val context: Context, var listItemMenu: MutableList<ProdukModel.Data>) :
    RecyclerView.Adapter<ProdukAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(produks: ProdukModel.Data, context: Context) {
            val uriImage = "http://belipulsabeta.purja.web.id/public/vendor/crudbooster/avatar.jpg"
            if (produks.image == null){
                produks.image = uriImage
            }
            Glide.with(context)
                .load(Uri.parse(produks.image ))
                .placeholder(R.drawable.bni)
                .into(itemView.findViewById(R.id.IvItemProduk))
            itemView.findViewById<TextView>(R.id.TvHargaProduk).setText(produks.harga.toString())
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
                val popupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.menu_edit_item, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menuEdit -> editItem(produks)
                        R.id.menuHapus -> hapusItem(produks)
                    }

                    return@setOnMenuItemClickListener true

                }

                popupMenu.show()


            }


        }


    }

    private fun hapusItem(produks: ProdukModel.Data) {

    }

    private fun editItem(produks: ProdukModel.Data) {

    }

    override fun getItemCount(): Int = listItemMenu.size
}