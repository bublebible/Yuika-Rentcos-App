package myproject.yuikarentcos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import myproject.yuikarentcos.R
import myproject.yuikarentcos.model.Kostum

class KostumAdapter(private val list: List<Kostum>) : RecyclerView.Adapter<KostumAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tv_item_name) // Pastikan ID di xml sama
        // Tambahkan binding image dan harga disini
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kostum, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]

        // Panggil variabel baru yang sudah rapi (camelCase)
        holder.tvNama.text = item.namaKarakter
        holder.tvHarga.text = "Rp ${item.hargaSewa}"
        holder.tvStatus.text = item.status

        Glide.with(holder.itemView.context)
            .load(item.gambarUrl) // Panggil gambarUrl
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.imgFoto) // Pastikan ID ini img_item_photo di XML
    }

    override fun getItemCount(): Int = list.size
}