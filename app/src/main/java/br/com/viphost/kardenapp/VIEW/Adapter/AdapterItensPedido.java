package br.com.viphost.kardenapp.VIEW.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.MODEL.ItemPedido;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Holder.ViewH;

public class AdapterItensPedido extends RecyclerView.Adapter<ViewH> {

    private Context context;
    private ArrayList<ItemPedido> itemPedido;




    public AdapterItensPedido(Context context, ArrayList<ItemPedido> itemPedido) {
        this.context = context;
        this.itemPedido = itemPedido;
    }

    @NonNull
    @Override
    public ViewH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ip = LayoutInflater.from(context).inflate(R.layout.list_item_pedido,parent,false);
        return  new ViewH(ip);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewH holder, int position) {
        ItemPedido g = itemPedido.get(position);
        holder.txtNumeroPedido.setText(g.getNumeroPedido()+"");
        holder.txtNomeProdItem.setText(g.getNomeProduto());
        holder.txtQtdProduto.setText(g.getQuantidadeProduto()+"x");
        holder.totalItemProd.setText(g.getValorTotalPedidoStr());

    }

    @Override
    public int getItemCount() {
      return itemPedido.size();
    }
}
