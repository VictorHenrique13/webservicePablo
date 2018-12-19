package br.com.viphost.kardenapp.CONTROLLER.tipos;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;

public class ListaCategorias extends GraphqlResponse {
    private Categoria[] lista;
    private int count = 0;
    public ListaCategorias(Categoria[] lista){
        this.lista = lista;
    }

    public int tamanho(){
        return this.lista.length;
    }
    public Categoria get(int key){
        return this.lista[key];
    }
    public Categoria[] getAll(){
        return this.lista;
    }
    public boolean hasNext(){
        return this.count<this.tamanho();
    }
    public Categoria getNext(){
        this.count++;
        return this.lista[this.count-1];
    }
    public void resetCount(){
        this.count=0;
    }
}
