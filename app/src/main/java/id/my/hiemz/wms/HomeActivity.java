package id.my.hiemz.wms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

// Layar utama native: sapaan + daftar stok (GET /api/stocks) + tombol riwayat & keluar.
public class HomeActivity extends AppCompatActivity {
    private TextView tvSapa;
    private EditText etCari;
    private RecyclerView rv;
    private ProgressBar load;
    private StokAdapter adapter;
    private ArrayList<JSONObject> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_home);

        String user = getIntent().getStringExtra("username");
        String role = getIntent().getStringExtra("role");
        tvSapa = findViewById(R.id.tvSapa);
        tvSapa.setText("Halo, " + (user == null ? "" : user) + (role == null || role.isEmpty() ? "" : " (" + role + ")"));

        etCari = findViewById(R.id.etCari);
        rv = findViewById(R.id.rvStok);
        load = findViewById(R.id.loadHome);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StokAdapter(data);
        rv.setAdapter(adapter);

        Button btnCari = findViewById(R.id.btnCari);
        btnCari.setOnClickListener(v -> muat(etCari.getText().toString().trim()));

        Button btnRiwayat = findViewById(R.id.btnRiwayat);
        btnRiwayat.setOnClickListener(v -> startActivity(new Intent(this, RiwayatActivity.class)));

        Button btnWeb = findViewById(R.id.btnWeb);
        btnWeb.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        Button btnOut = findViewById(R.id.btnOut);
        btnOut.setOnClickListener(v -> {
            getSharedPreferences("wms", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        muat("");
    }

    private void muat(String cari) {
        load.setVisibility(View.VISIBLE);
        new Thread(() -> {
            ApiClient.Hasil h = ApiClient.stok(cari);
            runOnUiThread(() -> {
                load.setVisibility(View.GONE);
                if (!h.ok || h.data == null) {
                    Toast.makeText(this, h.pesan, Toast.LENGTH_LONG).show();
                    return;
                }
                data.clear();
                JSONArray arr = h.data.optJSONArray("items");
                if (arr != null) for (int i = 0; i < arr.length(); i++) data.add(arr.optJSONObject(i));
                adapter.notifyDataSetChanged();
                int total = h.data.optInt("total", data.size());
                tvSapa.setText(tvSapa.getText() + "\n" + total + " baris stok");
            });
        }).start();
    }
}
