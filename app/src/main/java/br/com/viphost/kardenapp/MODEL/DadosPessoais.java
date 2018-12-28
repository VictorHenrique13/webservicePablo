package br.com.viphost.kardenapp.MODEL;

public class DadosPessoais {
    private String nome;
    private String email;
    private String telefone;

    public DadosPessoais(String nome, String email, String telefone){
        this.nome=nome;
        this.email=email;
        this.telefone=telefone;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }
}
