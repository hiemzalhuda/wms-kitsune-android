package id.my.hiemz.wms

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Tambahan: GET /api/rekap?uid=&role= — sapaan, mutiara, kendaraan, IN/OUT/Rak/Langsir.
suspend fun ApiClient_rekap(uid: String, role: String): ApiClient.Hasil = withContext(Dispatchers.IO) {
    try {
        val u = "${ApiClient.BASE_URL}/rekap?uid=$uid&role=$role"
        val c = (URL(u).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000; readTimeout = 15000
        }
        val s = if (c.responseCode < 400) c.inputStream else c.errorStream
        val txt = s.bufferedReader().use { it.readText() }
        ApiClient.Hasil(true, "ok", JSONObject(txt))
    } catch (e: Exception) {
        ApiClient.Hasil(false, "Jaringan: ${e.message}", null)
    }
}
