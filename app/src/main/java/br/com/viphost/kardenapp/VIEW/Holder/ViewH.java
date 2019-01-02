package br.com.viphost.kardenapp.VIEW.Holder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.viphost.kardenapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewH extends RecyclerView.ViewHolder {
    public TextView txtTitleMesa;
    //--------------categoria itens------
    public TextView txtTitleCategoria;
    public ImageView imgCat;
    //----categoria single------------------
    public TextView txtTitleProdutoSingle;
    public TextView txtQuantidadeSingle;
    public TextView txtPrecoSingle;
    public ImageView add;
    public ImageView remove;
    //itens pedido
    public RecyclerView recyclerViewItensPeedido;
    public TextView txtTitleItensPedido;
    public TextView txtTotalComanda;
//---------------------
    public TextView txtNumeroPedido;
    public TextView txtNomeProdItem;
    public TextView txtQtdProduto;
    public TextView totalItemProd;
    //-----------------
    @SuppressLint("CutPasteId")
    public ViewH(@NonNull View itemView) {
        super(itemView);
        txtTitleMesa = itemView.findViewById(R.id.txtTitleMesa);
        //-------categoria----------------
        txtTitleCategoria = itemView.findViewById(R.id.txtTitleCategoria);
        imgCat = itemView.findViewById(R.id.imgCategoria);
        //------------------------ProdutoSingle----------
        txtTitleProdutoSingle = itemView.findViewById(R.id.txtTitleProdCat);
        txtQuantidadeSingle = itemView.findViewById(R.id.txtProdutoQuantidade);
        txtPrecoSingle = itemView.findViewById(R.id.txtProdutoPreco);
        add = itemView.findViewById(R.id.imgAddProd);
        remove = itemView.findViewById(R.id.imgRemoveProd);
        //-------------------------------------------------------------
        //itens pedido
        recyclerViewItensPeedido = itemView.findViewById(R.id.recyclerItensPedido);
        txtTitleItensPedido = itemView.findViewById(R.id.txtTitleItensPedidos);
        txtTotalComanda = itemView.findViewById(R.id.txtTotalPedidos);
        //itens list pedidos
        txtNumeroPedido = itemView.findViewById(R.id.txtNumeroPedido);
        txtNomeProdItem = itemView.findViewById(R.id.txtlistItemNome);
        txtQtdProduto = itemView.findViewById(R.id.txtQtdProduto);
        totalItemProd = itemView.findViewById(R.id.txtTotalItem);
    }
}
