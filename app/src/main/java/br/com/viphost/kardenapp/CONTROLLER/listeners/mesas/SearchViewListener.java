package br.com.viphost.kardenapp.CONTROLLER.listeners.mesas;

import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;

public class SearchViewListener implements SearchView.OnQueryTextListener {
    private final Activity activity;
    private final RecyclerView.Adapter adapter;
    private final ArrayList<String> array;
    private final DbOpenhelper DB;
    private String pesquisaAtual = "";
    public SearchViewListener(Activity activity, RecyclerView.Adapter adapter, ArrayList<String> array){
        this.activity=activity;
        this.adapter=adapter;
        this.array=array;
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
        ArrayList<String> newArray;
        if(partOfText!=null){
            newArray = DB.getListaMesas(partOfText);
        }else{
            newArray = DB.getListaMesas();
        }
        ArrayList<String> toAdd = new ArrayList<>();
        int count =0;
        int tamanho = array.size();
        int notifyRemove = -1;
        for(String nomeMesa : newArray){
            if(count<tamanho){
                if(array.get(count)!=nomeMesa){
                    array.set(count,nomeMesa);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }else{
                toAdd.add(nomeMesa);
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
