package id.my.hiemz.wms

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// Client API cold-api via proxy PHP. Semua panggil JSON, coroutine IO.
object ApiClient {
    // Via proxy PHP di host 8090 (jalur tunnel yg sudah ada)
    var BASE_URL = "https://wh.hiemz.my.id/api_proxy.php"

    data class Hasil(val ok: Boolean, val pesan: String, val data: JSONObject?)

    private fun baca(c: HttpURLConnection): String {
        val s = if (c.responseCode < 400) c.inputStream else c.errorStream
        return BufferedReader(InputStreamReader(s)).use { it.readText() }
    }

    // POST /api/login {username,password} -> token, username, role
    suspend fun login(user: String, pass: String): Hasil = withContext(Dispatchers.IO) {
        try {
            val c = (URL("$BASE_URL/login").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 15000; readTimeout = 15000
            }
            val body = JSONObject().put("username", user).put("password", pass).toString()
            c.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val j = JSONObject(baca(c))
            val ok = j.optString("status") == "ok"
            Hasil(ok, j.optString("message", if (ok) "Login berhasil" else "Login gagal"), j)
        } catch (e: Exception) {
            Hasil(false, "Jaringan: ${e.message}", null)
        }
    }

    // GET /api/stocks?limit=&search=
    suspend fun stok(cari: String = ""): Hasil = withContext(Dispatchers.IO) {
        try {
            var u = "$BASE_URL/stocks?limit=50"
            if (cari.isNotEmpty()) u += "&search=" + URLEncoder.encode(cari, "UTF-8")
            val c = (URL(u).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000; readTimeout = 15000
            }
            Hasil(true, "ok", JSONObject(baca(c)))
        } catch (e: Exception) {
            Hasil(false, "Jaringan: ${e.message}", null)
        }
    }

    // GET /api/transactions?limit=20
    suspend fun transaksi(): Hasil = withContext(Dispatchers.IO) {
        try {
            val c = (URL("$BASE_URL/transactions?limit=20").openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000; readTimeout = 15000
            }
            Hasil(true, "ok", JSONObject(baca(c)))
        } catch (e: Exception) {
            Hasil(false, "Jaringan: ${e.message}", null)
        }
    }

    // GET /api/materials?limit=50
    suspend fun material(): Hasil = withContext(Dispatchers.IO) {
        try {
            val c = (URL("$BASE_URL/materials?limit=50").openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000; readTimeout = 15000
            }
            Hasil(true, "ok", JSONObject(baca(c)))
        } catch (e: Exception) {
            Hasil(false, "Jaringan: ${e.message}", null)
        }
    }
}
