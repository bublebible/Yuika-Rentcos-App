package myproject.yuikarentcos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView // Import ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Import Glide
import myproject.yuikarentcos.R
import myproject.yuikarentcos.model.Kostum // Import Model Kostum

class KostumAdapter(private val list: List<Kostum>) : RecyclerView.Adapter<KostumAdapter.Holder>() {

    // Perbaikan: Definisikan SEMUA view yang dipakai di sini
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFoto: ImageView = view.findViewById(R.id.img_item_photo)
        val tvNama: TextView = view.findViewById(R.id.tv_item_name)
        val tvHarga: TextView = view.findViewById(R.id.tv_item_price)
        val tvStatus: TextView = view.findViewById(R.id.tv_item_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kostum, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]

        holder.tvNama.text = item.namaKarakter
        holder.tvHarga.text = "Rp ${item.hargaSewa}"
        holder.tvStatus.text = item.status

        // Glide sekarang sudah dikenali
        Glide.with(holder.itemView.context)
            .load(item.gambarUrl)
            .placeholder(R.drawable.ic_launcher_background) // Pastikan gambar ini ada atau ganti dengan icon lain
            .into(holder.imgFoto)
    }

    override fun getItemCount(): Int = list.size
}