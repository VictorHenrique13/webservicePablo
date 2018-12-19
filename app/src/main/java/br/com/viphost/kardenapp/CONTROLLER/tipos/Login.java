package br.com.viphost.kardenapp.CONTROLLER.tipos;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;

public class Login extends GraphqlResponse {
    private String token = "";
    private Integer permissao = -1;

    public String getToken(){
        return token;
    }

    public int getPermissao(){
        return permissao;
    }
}
