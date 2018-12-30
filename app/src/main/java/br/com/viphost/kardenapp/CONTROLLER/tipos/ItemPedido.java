package br.com.viphost.kardenapp.CONTROLLER.tipos;

public class ItemPedido {
    private Integer id;
    private Produto produto;
    private Integer quantidade;
    private Double valor;
    private Double acrescimoDesconto;
    private String comentario;

    public Integer getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Double getValor() {
        return valor;
    }

    public Double getAcrescimoDesconto() {
        return acrescimoDesconto;
    }

    public String getComentario() {
        return comentario;
    }
}
