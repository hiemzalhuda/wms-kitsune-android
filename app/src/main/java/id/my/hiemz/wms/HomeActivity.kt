package id.my.hiemz.wms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import id.my.hiemz.wms.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

// Home samakan web: hero sapaan + tombol Input/Stok + mutiara + kendaraan + rekap + tab.
class HomeActivity : AppCompatActivity() {
    private lateinit var b: ActivityHomeBinding
    private var tabKemarin = false
    private var jToday: JSONObject? = null
    private var jYest: JSONObject? = null

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)

        val sp = getSharedPreferences("wms", Context.MODE_PRIVATE)
        val user = intent.getStringExtra("username") ?: sp.getString("username", "") ?: ""
        val role = intent.getStringExtra("role") ?: sp.getString("role", "") ?: ""
        val uid = sp.getString("uid", "") ?: ""

        b.tvSapa.text = "Memuat..."
        b.loadHome.visibility = View.VISIBLE
        lifecycleScope.launch {
            val h = ApiClient_rekap(uid, role)
            b.loadHome.visibility = View.GONE
            if (!h.ok || h.data == null) {
                Toast.makeText(this@HomeActivity, h.pesan, Toast.LENGTH_LONG).show()
                return@launch
            }
            val d = h.data
            b.tvSapa.text = "${d.optString("sapaan", "Halo")}, $user"
            b.tvTanggal.text = d.optString("tanggal", "")
            b.tvMutiara.text = "\u201C${d.optString("mutiara", "")}\u201D"

            val kv = d.optJSONObject("kendaraan")
            val totKv = (kv?.optInt("menunggu", 0) ?: 0) + (kv?.optInt("proses", 0) ?: 0) + (kv?.optInt("selesai", 0) ?: 0)
            if (totKv > 0 && kv != null) {
                b.cardKendaraan.visibility = View.VISIBLE
                b.tvKvTunggu.text = "${kv.optInt("menunggu", 0)}"
                b.tvKvProses.text = "${kv.optInt("proses", 0)}"
                b.tvKvSelesai.text = "${kv.optInt("selesai", 0)}"
            } else {
                b.cardKendaraan.visibility = View.GONE
            }

            jToday = d.optJSONObject("today")
            jYest = d.optJSONObject("yesterday")
            tampilRekap()

            b.tabToday.setOnClickListener { tabKemarin = false; gayaTab(); tampilRekap() }
            b.tabYest.setOnClickListener { tabKemarin = true; gayaTab(); tampilRekap() }
            gayaTab()
        }

        b.btnInput.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        b.btnStok.setOnClickListener {
            startActivity(Intent(this, StokActivity::class.java))
        }
        b.cardRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
        b.btnOut.setOnClickListener {
            getSharedPreferences("wms", Context.MODE_PRIVATE).edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun gayaTab() {
        b.tabToday.isSelected = !tabKemarin
        b.tabYest.isSelected = tabKemarin
        b.tabToday.alpha = if (!tabKemarin) 1f else 0.55f
        b.tabYest.alpha = if (tabKemarin) 1f else 0.55f
    }

    private fun tampilRekap() {
        val j = (if (tabKemarin) jYest else jToday) ?: return
        val nf = NumberFormat.getNumberInstance(Locale("in", "ID")).apply { maximumFractionDigits = 0 }
        b.tvIn.text = nf.format(j.optDouble("in", 0.0))
        b.tvOut.text = nf.format(j.optDouble("out", 0.0))
        b.tvRak.text = nf.format(j.optDouble("rak", 0.0))
        b.tvLangsir.text = "${j.optInt("langsir", 0)}x"
        b.tvLangsirKg.text = "${nf.format(j.optDouble("langsir_kg", 0.0))} KG"
        b.tvRekapSub.text = if (tabKemarin) "KG Kemarin" else "KG Hari Ini"
    }
}
