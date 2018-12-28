package br.com.viphost.kardenapp.CONTROLLER.tipos;

import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;

public class Login extends GraphqlResponse {
    private String nome = "";
    private String telefone = "";
    private String token = "";
    private Integer permissao = -1;

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getToken(){
        return token;
    }

    public int getPermissao(){
        return permissao;
    }
}
