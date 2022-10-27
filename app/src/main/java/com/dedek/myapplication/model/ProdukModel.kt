package com.dedek.myapplication.model

data class ProdukModel(
    val api_message: String,
    val api_status: Int,
    val `data`: List<Data>
) {
    data class Data(
        val harga: Int,
        val id: Int,
        var image: String,
        val nama_produk: String,
        val status: String
    )
}