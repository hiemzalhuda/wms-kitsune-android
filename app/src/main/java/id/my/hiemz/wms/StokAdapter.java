package id.my.hiemz.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import java.util.ArrayList;

// Adapter baris stok: kode + deskripsi + sloc/batch + qty.
public class StokAdapter extends RecyclerView.Adapter<StokAdapter.VH> {
    private ArrayList<JSONObject> data;
    public StokAdapter(ArrayList<JSONObject> d) { data = d; }

    static class VH extends RecyclerView.ViewHolder {
        TextView t1, t2, t3;
        VH(View v) { super(v); t1 = v.findViewById(R.id.rowJudul); t2 = v.findViewById(R.id.rowSub); t3 = v.findViewById(R.id.rowQty); }
    }

    @Override public VH onCreateViewHolder(ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.row_stok, p, false));
    }
    @Override public void onBindViewHolder(VH h, int i) {
        JSONObject o = data.get(i);
        h.t1.setText(o.optString("kode_material", "-") + " • " + o.optString("deskripsi", "-"));
        h.t2.setText("SLOC " + o.optString("sloc", "-") + " • Batch " + o.optString("batch", "-"));
        double q = o.optDouble("qty", 0);
        h.t3.setText(String.valueOf(q) + " " + o.optString("unit", "KG"));
    }
    @Override public int getItemCount() { return data.size(); }
}
