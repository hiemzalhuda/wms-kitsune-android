package id.my.hiemz.wms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.my.hiemz.wms.databinding.RowTeksBinding

// Adapter teks sederhana (layar riwayat).
class TeksAdapter(private val data: ArrayList<String>) : RecyclerView.Adapter<TeksAdapter.VH>() {
    class VH(val b: RowTeksBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(p: ViewGroup, t: Int): VH =
        VH(RowTeksBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: VH, i: Int) { h.b.rowTeks.text = data[i] }
    override fun getItemCount() = data.size
}
