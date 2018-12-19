package br.com.viphost.kardenapp.CONTROLLER.tipos;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;

public class Mesa extends GraphqlResponse {
    private int comanda;
    private boolean jaAberta;

    public int getComanda() {
        return comanda;
    }

    public boolean getJaAberta() {
        return jaAberta;
    }
}
