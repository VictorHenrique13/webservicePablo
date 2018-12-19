package br.com.viphost.kardenapp.CONTROLLER.tipos;


import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;

public class IntArray extends GraphqlResponse {
    private int[] array;
    private int count = 0;
    public IntArray(Integer[] array){
        this.array = new int[array.length];
        for(int x = 0 ; x<array.length ; x++){
            this.array[x] = array[x];
        }
    }

    public int tamanho(){
        return this.array.length;
    }
    public int get(int key){
        return this.array[key];
    }
    public int[] getAll(){
        return this.array;
    }
    public boolean hasNext(){
        return this.count<this.tamanho();
    }
    public int getNext(){
        this.count++;
        return this.array[this.count-1];
    }
    public void resetCount(){
        this.count=0;
    }
}
