package br.com.viphost.kardenapp.VIEW.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Holder.ViewH;

public class AdapterPedidos extends RecyclerView.Adapter<ViewH> {

    private Context context;
    private ArrayList<String> pedidos;

    public AdapterPedidos(Context context, ArrayList<String> pedidos) {
        this.context = context;
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public ViewH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.list_mesas,parent,false);
        return new ViewH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewH holder, int position) {
        holder.txtTitleMesa.setText(pedidos.get(position));
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }
}
