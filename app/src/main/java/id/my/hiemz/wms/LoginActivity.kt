package id.my.hiemz.wms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import id.my.hiemz.wms.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

// Layar login FINO: panggil POST /api/login, simpan sesi.
class LoginActivity : AppCompatActivity() {
    private lateinit var b: ActivityLoginBinding

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        val sp = getSharedPreferences("wms", Context.MODE_PRIVATE)
        if (sp.contains("token")) {
            masuk(sp.getString("username", "") ?: "", sp.getString("role", "") ?: "")
            return
        }

        b.btnLogin.setOnClickListener {
            val u = b.etUser.text.toString().trim()
            val p = b.etPass.text.toString()
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Isi username & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            b.loadLogin.visibility = View.VISIBLE
            b.btnLogin.isEnabled = false
            lifecycleScope.launch {
                val h = ApiClient.login(u, p)
                b.loadLogin.visibility = View.GONE
                b.btnLogin.isEnabled = true
                if (h.ok && h.data != null) {
                    val uname = h.data.optString("username", u)
                    val role = h.data.optString("role", "")
                    val token = h.data.optString("token", "")
                    getSharedPreferences("wms", Context.MODE_PRIVATE).edit()
                        .putString("token", token)
                        .putString("username", uname)
                        .putString("role", role).apply()
                    masuk(uname, role)
                } else {
                    Toast.makeText(this@LoginActivity, h.pesan, Toast.LENGTH_LONG).show()
                }
            }
        }

        b.tvWeb.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun masuk(user: String, role: String) {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            putExtra("username", user); putExtra("role", role)
        })
        finish()
    }
}
