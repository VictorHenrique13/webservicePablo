package br.com.viphost.kardenapp.CONTROLLER.tipos;

public class Listagem {
    private Integer[] mesas = {};
    private Categoria[] categorias = {};
    private Produto[] produtos = {};

    public Integer[] getMesas() {
        return mesas;
    }

    public ListaCategorias getCategorias() {
        return new ListaCategorias(categorias);
    }

    public ListaProdutos getProdutos() {
        return new ListaProdutos(produtos);
    }
}
