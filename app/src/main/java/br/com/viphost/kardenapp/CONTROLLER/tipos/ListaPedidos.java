package br.com.viphost.kardenapp.CONTROLLER.tipos;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;

public class ListaPedidos extends GraphqlResponse {
    private Pedido[] lista;
    private int count = 0;
    public ListaPedidos(Pedido[] lista){
        this.lista = lista;
    }

    public int tamanho(){
        return this.lista.length;
    }
    public Pedido get(int key){
        return this.lista[key];
    }
    public Pedido[] getAll(){
        return this.lista;
    }
    public boolean hasNext(){
        return this.count<this.tamanho();
    }
    public Pedido getNext(){
        this.count++;
        return this.lista[this.count-1];
    }
    public void resetCount(){
        this.count=0;
    }
}
