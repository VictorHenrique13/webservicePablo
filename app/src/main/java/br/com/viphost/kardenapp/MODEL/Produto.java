package br.com.viphost.kardenapp.MODEL;

public class Produto {
    private int id;
    private String nome;
    private String nameCategoria;
    private int quantidade;
    private  Float preco;

    public Produto(Integer id, String nome, int quantidade, Float preco){
        this.id=id;
        this.nome=nome;
        this.quantidade=quantidade;
        this.preco=preco;
    }
    public Produto(int id, String nome, int quantidade, Float preco,String nameCategoria){
        this.id=id;
        this.nome=nome;
        this.quantidade=quantidade;
        this.preco=preco;
        this.nameCategoria = nameCategoria;
    }

    public String getNameCategoria() {
        return nameCategoria;
    }

    public void setNameCategoria(String nameCategoria) {
        this.nameCategoria = nameCategoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getQuantidade() {
        return Integer.toString(quantidade);
    }
    public int getIntQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getPreco() {
        return String.format("R$ %.2f", preco);
    }
    public Float getFloatPreco() {
        return preco;
    }

    public void setPreco(Float preco) {
        this.preco = preco;
    }
}
