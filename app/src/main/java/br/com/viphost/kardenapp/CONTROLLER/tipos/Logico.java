package br.com.viphost.kardenapp.CONTROLLER.tipos;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;

public class Logico extends GraphqlResponse {
    private boolean valor;

    public Logico (boolean a){
        valor=a;
    }
    public boolean getValor(){
        return valor;
    }
}
