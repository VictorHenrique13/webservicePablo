package br.com.viphost.kardenapp.CONTROLLER.connections.pagecategoria;

import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarCategorias;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarProdutos;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Categoria;
import br.com.viphost.kardenapp.CONTROLLER.tipos.ListaCategorias;
import br.com.viphost.kardenapp.CONTROLLER.tipos.ListaProdutos;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterSingleCategoria;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterWithIcon;
import br.com.viphost.kardenapp.VIEW.PageCategoria;

public class AtualizarProdutos {
    private RecyclerView recyclerView;
    private String categoriaNome;
    private ArrayList<Produto> prod;
    private Activity activity;
    private AdapterSingleCategoria adp;
    private DbOpenhelper DB;

    public AtualizarProdutos(Activity activity, String categoriaNome, ArrayList<Produto> prod/*, ArrayList<Integer> f_ids*/, RecyclerView recyclerView, AdapterSingleCategoria adp){
        this.recyclerView=recyclerView;
        this.categoriaNome=categoriaNome;
        this.prod=prod;
        //this.f_ids=f_ids;
        this.activity=activity;
        this.adp=adp;
        DB = new DbOpenhelper(activity);
    }
    private void atualizarRecycler(){
        ArrayList<Produto> array = DB.getListaProdutos(categoriaNome);
        ArrayList<Produto> toAdd = new ArrayList<>();
        int count =0;
        int tamanho = prod.size();
        int notifyRemove = -1;
        for(Produto produto : array){
            if(count<tamanho){
                if(prod.get(count).getId()!=produto.getId()){
                    prod.set(count,produto);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adp.notifyDataSetChanged();
                        }
                    });
                }
            }else{
                toAdd.add(produto);
            }
            count++;
        }
        int index = count;
        while(count<tamanho){
            prod.remove(index);
            if(notifyRemove==-1){
                notifyRemove=count;
            }
            count++;
        }
        if(notifyRemove>-1){
            final int init = notifyRemove;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //recyclerView.removeViews(init,tamanho);
                    adp.notifyItemRangeRemoved(init,tamanho-init);
                    //adp.notifyItemRangeChanged(init, prod.size());
                }
            });
        }
        if(toAdd.size()>0){
            prod.addAll(tamanho, toAdd);
            final int finalIndex = tamanho+1;
            final int finalSize = toAdd.size();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adp.notifyItemRangeInserted(finalIndex, finalSize);
                }
            });
        }
    }
    public void run(){
        atualizarRecycler();
        final GraphqlClient graphqlClient = new GraphqlClient();
        final ListarProdutos listarProdutos = new ListarProdutos(graphqlClient);
        final String deviceID = new DeviceInfo().getDeviceID(this.activity.getApplicationContext());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GraphqlResponse resposta = listarProdutos.run(categoriaNome, DB.getToken(),deviceID);
                if(resposta instanceof ListaProdutos){
                    boolean needUpdate = false;
                    ListaProdutos array =(ListaProdutos) resposta;
                    ArrayList<Produto> toAdd = new ArrayList<>();
                    ArrayList<Produto> DbArrayProdutos = DB.getListaProdutos(categoriaNome);
                    int count =0;
                    int tamanho = DbArrayProdutos.size();
                    while(array.hasNext()){
                        br.com.viphost.kardenapp.CONTROLLER.tipos.Produto next = array.getNext();
                        Produto novo_produto = new Produto(next.getId(),next.getNome(),0,next.getValor(),categoriaNome);
                        if(count<tamanho){
                            Produto produtoDb = DbArrayProdutos.get(count);
                            if(DbArrayProdutos.get(count).getId()!=next.getId()){
                                DB.updateProduto(produtoDb,novo_produto);//DbArrayProdutos.set(count,novo_produto);
                                needUpdate=true;
                            }
                        }else{
                            toAdd.add(novo_produto);
                        }
                        count++;
                    }
                    while(count<tamanho){
                        Produto produtoDb = DbArrayProdutos.get(count);
                        DB.removeProduto(produtoDb.getId());//DbArrayProdutos.remove(count);
                        needUpdate=true;
                        count++;
                    }
                    if(toAdd.size()>0){
                        needUpdate=true;
                        for(Produto produtoToAdd : toAdd) {//DbArrayProdutos.addAll(tamanho, toAdd);
                            DB.insertProduto(produtoToAdd);
                        }
                    }
                    if(needUpdate){
                        atualizarRecycler();
                    }
                }else if(resposta instanceof GraphqlError){
                    final GraphqlError error = (GraphqlError) resposta;
                    if(error.getCode()==13){//Nenhum produto encontrado nessa categoria
                        DB.removeAllProdutoFromCategoria(categoriaNome);
                        atualizarRecycler();
                    }
                    new Balao(activity, error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_LONG).show();
                } else{
                    new Balao(activity,"Erro desconhecido",Toast.LENGTH_LONG).show();
                }
            }
        };
        new Thread(runnable).start();
    }
}
