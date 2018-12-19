package br.com.viphost.kardenapp.CONTROLLER.tipos;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;

public class ListaProdutos extends GraphqlResponse {
    private Produto[] lista;
    private int count = 0;
    public ListaProdutos(Produto[] lista){
        this.lista = lista;
    }

    public int tamanho(){
        return this.lista.length;
    }
    public Produto get(int key){
        return this.lista[key];
    }
    public Produto[] getAll(){
        return this.lista;
    }
    public boolean hasNext(){
        return this.count<this.tamanho();
    }
    public Produto getNext(){
        this.count++;
        return this.lista[this.count-1];
    }
    public void resetCount(){
        this.count=0;
    }
}
