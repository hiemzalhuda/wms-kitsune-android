package id.my.hiemz.wms

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.my.hiemz.wms.databinding.ActivityStokBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

// Layar stok: cari + daftar kartu.
class StokActivity : AppCompatActivity() {
    private lateinit var b: ActivityStokBinding

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityStokBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.rvStok.layoutManager = LinearLayoutManager(this)

        b.btnCari.setOnClickListener { muat(b.etCari.text.toString().trim()) }
        muat("")
    }

    private fun muat(cari: String) {
        b.loadStok.visibility = View.VISIBLE
        lifecycleScope.launch {
            val h = ApiClient.stok(cari)
            b.loadStok.visibility = View.GONE
            if (!h.ok || h.data == null) {
                Toast.makeText(this@StokActivity, h.pesan, Toast.LENGTH_LONG).show()
                return@launch
            }
            val list = ArrayList<JSONObject>()
            val arr = h.data.optJSONArray("items")
            if (arr != null) for (i in 0 until arr.length()) {
                arr.optJSONObject(i)?.let { list.add(it) }
            }
            b.tvInfoStok.text = if (list.isEmpty()) "Tidak ada data." else "${list.size} item stok"
            b.rvStok.adapter = StokAdapter(list)
        }
    }
}
