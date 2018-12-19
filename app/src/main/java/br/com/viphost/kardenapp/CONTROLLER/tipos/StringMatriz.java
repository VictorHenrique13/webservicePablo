package br.com.viphost.kardenapp.CONTROLLER.tipos;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;

public class StringMatriz extends GraphqlResponse {
    private String[][] matriz;
    private int count = 0;
    public StringMatriz(String[][] matriz){
        this.matriz = matriz;
    }

    public int tamanho(){
        return this.matriz.length;
    }
    public String[] get(int key){
        return this.matriz[key];
    }
    public String get(int x, int y){
        return this.matriz[x][y];
    }
    public String[][] getAll(){
        return this.matriz;
    }
    public boolean hasNext(){
        return this.count<this.tamanho();
    }
    public String[] getNext(){
        this.count++;
        return this.matriz[this.count-1];
    }
    public void resetCount(){
        this.count=0;
    }
}
