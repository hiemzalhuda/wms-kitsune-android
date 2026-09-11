package id.my.hiemz.wms;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

// Client API cold-api (Go). Semua panggil JSON, tanpa library luar.
public class ApiClient {
    // Via proxy PHP di host 8090 (jalur tunnel yg sudah ada).
    // Format: https://wh.hiemz.my.id/api_proxy.php/<endpoint>
    public static String BASE_URL = "https://wh.hiemz.my.id/api_proxy.php";

    public static class Hasil {
        public boolean ok;
        public String pesan;
        public JSONObject data;
        public Hasil(boolean o, String p, JSONObject d) { ok = o; pesan = p; data = d; }
    }

    private static String baca(HttpURLConnection c) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(
            c.getResponseCode() < 400 ? c.getInputStream() : c.getErrorStream()));
        StringBuilder sb = new StringBuilder();
        String l; while ((l = br.readLine()) != null) sb.append(l);
        br.close();
        return sb.toString();
    }

    // POST /api/login {username,password} -> token, username, role
    public static Hasil login(String user, String pass) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(BASE_URL + "/login").openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("Content-Type", "application/json");
            c.setDoOutput(true);
            c.setConnectTimeout(15000); c.setReadTimeout(15000);
            JSONObject body = new JSONObject();
            body.put("username", user); body.put("password", pass);
            OutputStream os = c.getOutputStream();
            os.write(body.toString().getBytes("UTF-8")); os.close();
            JSONObject j = new JSONObject(baca(c));
            boolean ok = "ok".equals(j.optString("status"));
            return new Hasil(ok, j.optString("message", ok ? "Login berhasil" : "Login gagal"), j);
        } catch (Exception e) {
            return new Hasil(false, "Jaringan: " + e.getMessage(), null);
        }
    }

    // GET /api/stocks?limit=&search=
    public static Hasil stok(String cari) {
        try {
            String u = BASE_URL + "/stocks?limit=50";
            if (cari != null && !cari.isEmpty()) u += "&search=" + URLEncoder.encode(cari, "UTF-8");
            HttpURLConnection c = (HttpURLConnection) new URL(u).openConnection();
            c.setConnectTimeout(15000); c.setReadTimeout(15000);
            JSONObject j = new JSONObject(baca(c));
            return new Hasil(true, "ok", j);
        } catch (Exception e) {
            return new Hasil(false, "Jaringan: " + e.getMessage(), null);
        }
    }

    // GET /api/transactions?limit=20
    public static Hasil transaksi() {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(BASE_URL + "/transactions?limit=20").openConnection();
            c.setConnectTimeout(15000); c.setReadTimeout(15000);
            JSONObject j = new JSONObject(baca(c));
            return new Hasil(true, "ok", j);
        } catch (Exception e) {
            return new Hasil(false, "Jaringan: " + e.getMessage(), null);
        }
    }

    // GET /api/materials?limit=50
    public static Hasil material() {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(BASE_URL + "/materials?limit=50").openConnection();
            c.setConnectTimeout(15000); c.setReadTimeout(15000);
            JSONObject j = new JSONObject(baca(c));
            return new Hasil(true, "ok", j);
        } catch (Exception e) {
            return new Hasil(false, "Jaringan: " + e.getMessage(), null);
        }
    }
}
