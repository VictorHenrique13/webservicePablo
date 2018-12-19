package br.com.viphost.kardenapp.MODEL;

import java.util.ArrayList;

public class ItensPedido {
    private ArrayList<Produto> produtos;
    public ItensPedido(ArrayList<Produto> produtos){
        this.produtos=produtos;
    }

    public ArrayList<Produto> getProdutos() {
        return produtos;
    }
}
