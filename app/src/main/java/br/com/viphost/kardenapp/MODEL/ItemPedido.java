package br.com.viphost.kardenapp.MODEL;

public class ItemPedido {
    private int numeroPedido; //começa sempre no 1
    private Produto produto;

    public ItemPedido(int numeroPedido, Produto produto){
        this.numeroPedido=numeroPedido;
        this.produto=produto;
    }

    public int getNumeroPedido() {
        return numeroPedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public String getNomeProduto(){
        return produto.getNome();
    }

    public int getQuantidadeProduto(){
        return produto.getIntQuantidade();
    }

    public double getValorUnitarioProduto(){
        return produto.getDoublePreco();
    }

    public double getValorTotalPedido(){
        return produto.getIntQuantidade()*produto.getDoublePreco();
    }
    public String getValorTotalPedidoStr(){
        return String.format("R$ %.2f",produto.getIntQuantidade()*produto.getDoublePreco());
    }
}
