package id.my.hiemz.wms;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

// Layar riwayat transaksi (GET /api/transactions, 20 terakhir).
public class RiwayatActivity extends AppCompatActivity {
    private RecyclerView rv;
    private ProgressBar load;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_riwayat);
        rv = findViewById(R.id.rvRiwayat);
        load = findViewById(R.id.loadRiwayat);
        tvInfo = findViewById(R.id.tvInfoRiwayat);
        rv.setLayoutManager(new LinearLayoutManager(this));

        load.setVisibility(View.VISIBLE);
        new Thread(() -> {
            ApiClient.Hasil h = ApiClient.transaksi();
            runOnUiThread(() -> {
                load.setVisibility(View.GONE);
                if (!h.ok || h.data == null) {
                    Toast.makeText(this, h.pesan, Toast.LENGTH_LONG).show();
                    return;
                }
                ArrayList<String> baris = new ArrayList<>();
                JSONArray arr = h.data.optJSONArray("items");
                if (arr != null) for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.optJSONObject(i);
                    baris.add("#" + o.optInt("id") + " " + o.optString("type", "-")
                        + " • " + o.optString("material_number", "-")
                        + " • " + o.optDouble("qty", 0)
                        + " • " + o.optString("start_time", ""));
                }
                if (baris.isEmpty()) tvInfo.setText("Belum ada transaksi.");
                else { tvInfo.setText(baris.size() + " transaksi terakhir:"); rv.setAdapter(new TeksAdapter(baris)); }
            });
        }).start();
    }
}
