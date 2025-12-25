package myproject.yuikarentcos.model

import com.google.gson.annotations.SerializedName

data class Kostum(
    val id: Int,

    @SerializedName("nama_karakter")
    val namaKarakter: String,

    @SerializedName("harga_sewa")
    val hargaSewa: Int,

    @SerializedName("gambar_url")
    val gambarUrl: String,

    val status: String
)