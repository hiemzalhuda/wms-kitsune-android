package id.my.hiemz.wms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

// Adapter teks sederhana (dipakai layar riwayat).
public class TeksAdapter extends RecyclerView.Adapter<TeksAdapter.VH> {
    private ArrayList<String> data;
    public TeksAdapter(ArrayList<String> d) { data = d; }
    static class VH extends RecyclerView.ViewHolder {
        TextView t;
        VH(View v) { super(v); t = v.findViewById(R.id.rowTeks); }
    }
    @Override public VH onCreateViewHolder(ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.row_teks, p, false));
    }
    @Override public void onBindViewHolder(VH h, int i) { h.t.setText(data.get(i)); }
    @Override public int getItemCount() { return data.size(); }
}
