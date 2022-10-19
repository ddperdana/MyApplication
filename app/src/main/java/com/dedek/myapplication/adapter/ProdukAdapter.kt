package com.dedek.myapplication.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dedek.myapplication.R
import com.dedek.myapplication.model.ProdukModel

class ProdukAdapter(val context: Context, var listItemMenu: List<ProdukModel.Data?>?) :
    RecyclerView.Adapter<ProdukAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(produks: ProdukModel.Data, context: Context) {
            Glide.with(context)
                .load(Uri.parse(produks.image))
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
        val item = listItemMenu?.get(position)
        if (item != null) {
            holder.onBind(item, context)


        }


    }

    override fun getItemCount(): Int = listItemMenu!!.size
}