package br.com.viphost.kardenapp.CONTROLLER.listeners.pagecategoria;

import android.app.Activity;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.MODEL.Produto;

public class SearchViewListener implements SearchView.OnQueryTextListener {
    private final Activity activity;
    private final RecyclerView.Adapter adapter;
    private final ArrayList<Produto> array;
    private final DbOpenhelper DB;
    private final String categoria;
    private String pesquisaAtual = "";
    public SearchViewListener(Activity activity, RecyclerView.Adapter adapter, ArrayList<Produto> array, String categoria){
        this.activity=activity;
        this.adapter=adapter;
        this.array=array;
        this.categoria=categoria;
        DB=new DbOpenhelper(activity);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        pesquisaAtual=newText;
        if(newText.isEmpty()){
            atualizarRecycler(null);
        }else{
            atualizarRecycler(newText);
        }
        return true;
    }
    public void updateOnShow(){
        if(pesquisaAtual.isEmpty()){
            atualizarRecycler(null);
        }else{
            atualizarRecycler(pesquisaAtual);
        }
    }
    private void atualizarRecycler(String partOfText){
        ArrayList<Produto> newArray;
        if(partOfText!=null){
            newArray = DB.getListaProdutos(categoria, partOfText);
        }else{
            newArray = DB.getListaProdutos(categoria);
        }
        ArrayList<Produto> toAdd = new ArrayList<>();
        int count =0;
        int tamanho = array.size();
        int notifyRemove = -1;
        for(Produto produto : newArray){
            if(count<tamanho){
                if(array.get(count).getId()!=produto.getId()){
                    array.set(count,produto);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
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
            array.remove(index);
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
                    adapter.notifyItemRangeRemoved(init,tamanho-init);
                    //adp.notifyItemRangeChanged(init, prod.size());
                }
            });
        }
        if(toAdd.size()>0){
            array.addAll(tamanho, toAdd);
            final int finalIndex = tamanho+1;
            final int finalSize = toAdd.size();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemRangeInserted(finalIndex, finalSize);
                }
            });
        }
    }
}
