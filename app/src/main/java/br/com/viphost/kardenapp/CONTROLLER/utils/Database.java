package br.com.viphost.kardenapp.CONTROLLER.utils;

import android.content.Context;
import android.content.Intent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import br.com.viphost.kardenapp.MODEL.ItensPedido;
import br.com.viphost.kardenapp.MODEL.Produto;

public class Database {
    private static String mesaAtual = "-1";
    private static ArrayList<String> categorias_nomes = null;
    private static ArrayList<Integer> categorias_ids = null;
    private static Hashtable<String, ItensPedido> carrinho_mesas = new Hashtable<>();
    private static Hashtable<String, Integer> comanda_mesas = new Hashtable<>();

    public static void setCategorias(ArrayList<String> nomes, ArrayList<Integer> ids){
        categorias_nomes = nomes;
        categorias_ids = ids;
    }
    public static ItensPedido getCarrinho(String mesa){
        if(carrinho_mesas.containsKey(mesa)){
            return carrinho_mesas.get(mesa);
        }else{
            return new ItensPedido(new ArrayList<Produto>());
        }
    }
    public static void apagarCarrinhoMesa(String mesa){
        if(carrinho_mesas.containsKey(mesa)){
            carrinho_mesas.remove(mesa);
        }
    }
    public static ItensPedido getCarrinhoMesaAtual(){
        if(carrinho_mesas.containsKey(getMesaAtual())){
            return carrinho_mesas.get(getMesaAtual());
        }else{
            return new ItensPedido(new ArrayList<Produto>());
        }
    }
//    public static void addToCarrinho(int Mesa, ArrayList<Produto> produtos){
//        if(carrinho_mesas.containsKey(Mesa)){
//            carrinho_mesas.get(Mesa).getProdutos().addAll(produtos);
//        }else{
//            carrinho_mesas.put(Mesa, new ItensPedido(produtos));
//        }
//    }
//    public static void addToCarrinho(int Mesa, Produto produto){
//        if(carrinho_mesas.containsKey(Mesa)){
//            carrinho_mesas.get(Mesa).getProdutos().add(produto);
//        }else{
//            ArrayList<Produto> produtos = new ArrayList<>();
//            produtos.add(produto);
//            carrinho_mesas.put(Mesa, new ItensPedido(produtos));
//        }
//    }
    public static void setToCarrinho(String mesa, Produto produto){
        if(carrinho_mesas.containsKey(mesa)){
            ArrayList<Produto> produtos = carrinho_mesas.get(mesa).getProdutos();
            for(Produto prod : produtos){
                if(prod.getId()==produto.getId()){
                    prod.setQuantidade(produto.getIntQuantidade());
                    return;
                }
            }
            produtos.add(produto);
        }else{
            ArrayList<Produto> produtos = new ArrayList<>();
            produtos.add(produto);
            carrinho_mesas.put(mesa, new ItensPedido(produtos));
        }
    }
    public static void addToCarrinho(String mesa, Produto produto){
        produto.setQuantidade(1);
        if(carrinho_mesas.containsKey(mesa)){
            ArrayList<Produto> produtos = carrinho_mesas.get(mesa).getProdutos();
            for(Produto prod : produtos){
                if(prod.getId()==produto.getId()){
                    prod.setQuantidade(prod.getIntQuantidade()+1);
                    return;
                }
            }
            Produto toAdd = new Produto(produto.getId(),produto.getNome(),produto.getIntQuantidade(),produto.getFloatPreco());
            produtos.add(toAdd);
        }else{
            ArrayList<Produto> produtos = new ArrayList<>();
            Produto toAdd = new Produto(produto.getId(),produto.getNome(),produto.getIntQuantidade(),produto.getFloatPreco());
            produtos.add(toAdd);
            carrinho_mesas.put(mesa, new ItensPedido(produtos));
        }
    }
    public static void removeToCarrinho(String mesa, Produto produto){
        produto.setQuantidade(1);
        if(carrinho_mesas.containsKey(mesa)){
            ArrayList<Produto> produtos = carrinho_mesas.get(mesa).getProdutos();
            for(Produto prod : produtos){
                if(prod.getId()==produto.getId()){
                    prod.setQuantidade(prod.getIntQuantidade()-1);
                    if(prod.getIntQuantidade()<1){
                        produtos.remove(produtos.indexOf(prod));
                    }
                    return;
                }
            }
        }
    }
    public static int getQntCarrinho(String mesa, Produto produto){
        if(carrinho_mesas.containsKey(mesa)){
            ArrayList<Produto> produtos = carrinho_mesas.get(mesa).getProdutos();
            for(Produto prod : produtos){
                if(prod.getId()==produto.getId()){
                    return prod.getIntQuantidade();
                }
            }
        }
        return 0;
    }
    public static String getMesaAtual(){
        return mesaAtual;
    }
    public static void setMesaAtual(String mesa){
        mesaAtual = mesa;
    }
    public static int getAtualComanda(String mesa){
        if(comanda_mesas.containsKey(mesa)){
            return comanda_mesas.get(mesa);
        }else{
            return -1;
        }
    }
    public static void setAtualComanda(String mesa, int comanda){
        comanda_mesas.put(mesa,comanda);
    }
    public static int getIdFromCategoria(String nome){
        if (categorias_ids==null || categorias_nomes==null)
            return -1;
        int index = categorias_nomes.lastIndexOf(nome);
        return categorias_ids.get(index);
    }

    public static String getToken(Context context){
        String TOKEN = "token";
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(TOKEN);
            Scanner scan = new Scanner(fis);
            if(scan.hasNext()){
                return scan.next();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static int getPermission(Context context){
        String TOKEN = "permissao";
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(TOKEN);
            Scanner scan = new Scanner(fis);
            if(scan.hasNextInt()){
                return scan.nextInt();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return -1;
    }
    public static void setToken(Context context, String token){
        String TOKEN = "token";

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(TOKEN, Context.MODE_PRIVATE);
            fos.write(token.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void setPermission(Context context, int permission){
        String PERMISSAO = "permissao";

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(PERMISSAO, Context.MODE_PRIVATE);
            fos.write((permission+"").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean tokenIsEmpty(Context context){
        String TOKEN = "token";

        FileInputStream fis = null;
            try {
            fis = context.openFileInput(TOKEN);
            Scanner scan = new Scanner(fis);
            if(scan.hasNext() && !scan.next().isEmpty()){
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
    public static boolean permissionIsEmpty(Context context){
        String TOKEN = "permissao";

        FileInputStream fis = null;
        try {
            fis = context.openFileInput(TOKEN);
            Scanner scan = new Scanner(fis);
            if(scan.hasNext() && !scan.next().isEmpty()){
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
}
