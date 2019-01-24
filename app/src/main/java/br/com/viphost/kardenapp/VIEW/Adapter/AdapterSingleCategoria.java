package br.com.viphost.kardenapp.VIEW.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.viphost.kardenapp.CONTROLLER.utils.Memoria;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Holder.ViewH;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterSingleCategoria extends RecyclerView.Adapter<ViewH> {
    private Activity context;
    private ArrayList<Produto> produtos;

    public AdapterSingleCategoria(Activity context, ArrayList<Produto> produtos) {
        this.context = context;
        this.produtos = produtos;
    }

    @NonNull
    @Override
    public ViewH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View f = LayoutInflater.from(context).inflate(R.layout.list_produto,parent,false);
        return new ViewH(f);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewH holder, int position) {
        final String mesa = Memoria.getMesaAtual();
        final Produto p = produtos.get(position);
        holder.txtTitleProdutoSingle.setText(p.getNome());
        //holder.txtQuantidadeSingle.setText(p.getQuantidade());
        holder.txtQuantidadeSingle.setText(Memoria.getQntCarrinho(mesa,p)+"");
        holder.txtPrecoSingle.setText(p.getPreco());
        holder.add.setClickable(true);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Memoria.addToCarrinho(mesa,p);
                notifyDataSetChanged();
            }
        });
        holder.remove.setClickable(true);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Memoria.removeToCarrinho(mesa,p);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
       int ver;
       if(produtos == null){
           ver =0;
       }else{
           ver = produtos.size();
       }
        return ver;
    }
}
