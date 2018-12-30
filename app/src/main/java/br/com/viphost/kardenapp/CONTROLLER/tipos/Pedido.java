package br.com.viphost.kardenapp.CONTROLLER.tipos;

public class Pedido {
    private ItemPedido[] itens;
    private Integer comanda;
    private String mesa;
    private Long timestamp;

    public ItemPedido[] getItens() {
        return itens;
    }

    public Integer getComanda() {
        return comanda;
    }

    public String getMesa() {
        return mesa;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
