package br.com.viphost.kardenapp.CONTROLLER.tipos;

public class Produto {
    private Integer id = -1;
    private String nome = "";
    private Float valor = -1.0f;
    private Integer categoria = -1;

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Float getValor() {
        return valor;
    }

    public Integer getCategoria() {
        return categoria;
    }
}
