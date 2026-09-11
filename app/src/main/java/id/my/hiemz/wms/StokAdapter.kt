package id.my.hiemz.wms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.my.hiemz.wms.databinding.RowStokBinding
import org.json.JSONObject

// Adapter baris stok FINO: badge qty hijau + kode + sloc/batch.
class StokAdapter(private val data: ArrayList<JSONObject>) : RecyclerView.Adapter<StokAdapter.VH>() {
    class VH(val b: RowStokBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(p: ViewGroup, t: Int): VH =
        VH(RowStokBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: VH, i: Int) {
        val o = data[i]
        h.b.rowJudul.text = "${o.optString("kode_material", "-")} • ${o.optString("deskripsi", "-")}"
        h.b.rowSub.text = "SLOC ${o.optString("sloc", "-")} • Batch ${o.optString("batch", "-")}"
        h.b.rowQty.text = "${o.optDouble("qty", 0.0)} ${o.optString("unit", "KG")}"
    }

    override fun getItemCount() = data.size
}
