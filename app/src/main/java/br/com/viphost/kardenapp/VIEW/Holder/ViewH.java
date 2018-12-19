package br.com.viphost.kardenapp.VIEW.Holder;

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
    //-----------------
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

    }
}
