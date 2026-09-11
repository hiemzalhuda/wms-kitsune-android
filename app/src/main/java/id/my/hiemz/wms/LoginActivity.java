package id.my.hiemz.wms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Layar login native: panggil POST /api/login, simpan sesi ke SharedPreferences.
public class LoginActivity extends AppCompatActivity {
    private EditText etUser, etPass;
    private Button btnLogin;
    private ProgressBar load;
    private TextView tvWeb;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        load = findViewById(R.id.loadLogin);
        tvWeb = findViewById(R.id.tvWeb);

        // Kalau sudah login, langsung masuk
        SharedPreferences sp = getSharedPreferences("wms", MODE_PRIVATE);
        if (sp.contains("token")) {
            masuk(sp.getString("username", ""), sp.getString("role", ""));
            return;
        }

        btnLogin.setOnClickListener(v -> {
            String u = etUser.getText().toString().trim();
            String p = etPass.getText().toString();
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Isi username & password", Toast.LENGTH_SHORT).show();
                return;
            }
            load.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            new Thread(() -> {
                ApiClient.Hasil h = ApiClient.login(u, p);
                runOnUiThread(() -> {
                    load.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    if (h.ok) {
                        String uname = h.data.optString("username", u);
                        String role = h.data.optString("role", "");
                        String token = h.data.optString("token", "");
                        getSharedPreferences("wms", MODE_PRIVATE).edit()
                            .putString("token", token)
                            .putString("username", uname)
                            .putString("role", role).apply();
                        masuk(uname, role);
                    } else {
                        Toast.makeText(this, h.pesan, Toast.LENGTH_LONG).show();
                    }
                });
            }).start();
        });

        // Link ke mode web lama (WebView) kalau API belum kebuka
        tvWeb.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
    }

    private void masuk(String user, String role) {
        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra("username", user); i.putExtra("role", role);
        startActivity(i);
        finish();
    }
}
