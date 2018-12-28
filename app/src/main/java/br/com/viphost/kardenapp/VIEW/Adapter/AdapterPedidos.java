package br.com.viphost.kardenapp.VIEW.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Holder.ViewH;

public class AdapterPedidos extends RecyclerView.Adapter<ViewH> {

    private Context context;
    private ArrayList<String> pedidos;
    private AlertDialog dialogDelete;
    private ArrayList<Produto> pr = new ArrayList<Produto>();
    public AdapterPedidos(Context context, ArrayList<String> pedidos) {
        this.context = context;
        this.pedidos = pedidos;
        // preencher pr com os dados de pedidos do server ou aqki ou la no click que pega qual mesa é e ja pega so os valores de itens daquela mesa vai ao seu criterio erlson
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
        holder.txtTitleMesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(context);
                View dp = LayoutInflater.from(context).inflate(R.layout.dialog_inf,null);
                TextView txtTitleItemPedido = dp.findViewById(R.id.txtTitleItensPedidos);
                TextView txtTotalComanda = dp.findViewById(R.id.txtTotalPedidos);
                RecyclerView recyclerView = dp.findViewById(R.id.recyclerItensPedido);
                RecyclerView.LayoutManager ln = new LinearLayoutManager(context);
                pr.add(new Produto(1,"Lasanha",10,10.00,"Massas"));
                AdapterItensPedido adp = new AdapterItensPedido(context,pr);
                recyclerView.setLayoutManager(ln);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adp);
                txtTitleItemPedido.setText(pedidos.get(position));
                txtTotalComanda.setText("Coloca o valor total aqui RElson");


                b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogDelete.dismiss();

                    }
                });
                b.setNegativeButton("Deletar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //codigo que deleta pedido dos bancos off e online

                    }
                });

                b.setView(dp);
                dialogDelete = b.create();
                dialogDelete.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }
}
