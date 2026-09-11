package id.my.hiemz.wms

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.my.hiemz.wms.databinding.ActivityRiwayatBinding
import kotlinx.coroutines.launch

// Layar riwayat transaksi (20 terakhir).
class RiwayatActivity : AppCompatActivity() {
    private lateinit var b: ActivityRiwayatBinding

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.rvRiwayat.layoutManager = LinearLayoutManager(this)

        b.loadRiwayat.visibility = View.VISIBLE
        lifecycleScope.launch {
            val h = ApiClient.transaksi()
            b.loadRiwayat.visibility = View.GONE
            if (!h.ok || h.data == null) {
                Toast.makeText(this@RiwayatActivity, h.pesan, Toast.LENGTH_LONG).show()
                return@launch
            }
            val baris = ArrayList<String>()
            val arr = h.data.optJSONArray("items")
            if (arr != null) for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                baris.add("#${o.optInt("id")} ${o.optString("type", "-")} • " +
                    "${o.optString("material_number", "-")} • ${o.optDouble("qty", 0.0)} • " +
                    o.optString("start_time", ""))
            }
            if (baris.isEmpty()) b.tvInfoRiwayat.text = "Belum ada transaksi."
            else {
                b.tvInfoRiwayat.text = "${baris.size} transaksi terakhir:"
                b.rvRiwayat.adapter = TeksAdapter(baris)
            }
        }
    }
}
