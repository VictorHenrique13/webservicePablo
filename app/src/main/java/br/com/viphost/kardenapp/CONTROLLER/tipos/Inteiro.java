package br.com.viphost.kardenapp.CONTROLLER.tipos;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;

public class Inteiro extends GraphqlResponse {
    private int valor;

    public Inteiro(int a){
        valor=a;
    }
    public int getValor(){
        return valor;
    }
}
