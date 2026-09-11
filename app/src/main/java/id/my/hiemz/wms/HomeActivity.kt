package id.my.hiemz.wms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.my.hiemz.wms.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

// Layar utama FINO: sapaan + kartu menu + daftar stok + cari.
class HomeActivity : AppCompatActivity() {
    private lateinit var b: ActivityHomeBinding
    private val data = ArrayList<JSONObject>()
    private lateinit var adapter: StokAdapter

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)

        val user = intent.getStringExtra("username") ?: ""
        val role = intent.getStringExtra("role") ?: ""
        b.tvSapa.text = "Halo, $user" + (if (role.isNotEmpty()) " ($role)" else "")

        b.rvStok.layoutManager = LinearLayoutManager(this)
        adapter = StokAdapter(data)
        b.rvStok.adapter = adapter

        b.btnCari.setOnClickListener { muat(b.etCari.text.toString().trim()) }
        b.cardRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
        b.cardWeb.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        b.btnOut.setOnClickListener {
            getSharedPreferences("wms", Context.MODE_PRIVATE).edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        muat("")
    }

    private fun muat(cari: String) {
        b.loadHome.visibility = View.VISIBLE
        lifecycleScope.launch {
            val h = ApiClient.stok(cari)
            b.loadHome.visibility = View.GONE
            if (!h.ok || h.data == null) {
                Toast.makeText(this@HomeActivity, h.pesan, Toast.LENGTH_LONG).show()
                return@launch
            }
            data.clear()
            val arr = h.data.optJSONArray("items")
            if (arr != null) for (i in 0 until arr.length()) {
                arr.optJSONObject(i)?.let { data.add(it) }
            }
            adapter.notifyDataSetChanged()
            val total = h.data.optInt("total", data.size)
            b.tvInfoStok.text = "$total baris stok" + (if (cari.isNotEmpty()) " • \"$cari\"" else "")
        }
    }
}
