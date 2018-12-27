package br.com.viphost.kardenapp.CONTROLLER.connections.categoria;

import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarCategorias;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarMesas;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Categoria;
import br.com.viphost.kardenapp.CONTROLLER.tipos.IntArray;
import br.com.viphost.kardenapp.CONTROLLER.tipos.ListaCategorias;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterNoIcon;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterWithIcon;

public class AtualizarCategorias {
    private RecyclerView recyclerView;
    private ArrayList<String> f;
    //private ArrayList<Integer> f_ids;
    private Activity activity;
    private AdapterWithIcon adp;
    private DbOpenhelper DB;

    public AtualizarCategorias(Activity activity, ArrayList<String> f/*, ArrayList<Integer> f_ids*/,  RecyclerView recyclerView, AdapterWithIcon adp){
        this.recyclerView=recyclerView;
        this.f=f;
        //this.f_ids=f_ids;
        this.activity=activity;
        this.adp=adp;
        DB = new DbOpenhelper(activity);
    }
    private void atualizarRecycler(){
        ArrayList<String> array = DB.getListaCategoria();
        ArrayList<String> toAdd = new ArrayList<>();
        int count =0;
        int tamanho = f.size();
        int notifyRemove = -1;
        for(String nomeCategoria : array){
            if(count<tamanho){
                if(f.get(count)!=nomeCategoria){
                    f.set(count,nomeCategoria);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adp.notifyDataSetChanged();
                        }
                    });
                }
            }else{
                toAdd.add(nomeCategoria);
            }
            count++;
        }
        int index = count;
        while(count<tamanho){
            f.remove(index);
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
            f.addAll(tamanho, toAdd);
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
        final ListarCategorias listarCategorias = new ListarCategorias(graphqlClient);
        final String deviceID = new DeviceInfo().getDeviceID(this.activity.getApplicationContext());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GraphqlResponse resposta = listarCategorias.run(DB.getToken(),deviceID);
                if(resposta instanceof ListaCategorias){
                    boolean needUpdate = false;
                    ListaCategorias array =(ListaCategorias) resposta;
                    ArrayList<String> toAdd = new ArrayList<>();
                    ArrayList<String> DbArrayCategorias = DB.getListaCategoria();
                    int count =0;
                    int tamanho = DbArrayCategorias.size();
                    while(array.hasNext()){
                        Categoria next = array.getNext();
                        if(count<tamanho){
                            String categoriaDb = DbArrayCategorias.get(count);
                            if(categoriaDb!=next.getNome()){
                                DB.updateCategoria(categoriaDb,next.getNome());//DbArrayCategorias.set(count,next.getNome());
                                needUpdate=true;
                            }
                        }else{
                            toAdd.add(next.getNome());
                        }
                        count++;
                    }
                    while(count<tamanho){
                        String categoriaDb = DbArrayCategorias.get(count);
                        DB.removeCategoria(categoriaDb);//DbArrayCategorias.remove(count);
                        needUpdate=true;
                        count++;
                    }
                    if(toAdd.size()>0){
                        needUpdate=true;
                        for(String categoriaToAdd : toAdd) {//DbArrayCategorias.addAll(tamanho, toAddStrings);
                            DB.insertCategoria(categoriaToAdd);
                        }
                    }
                    if(needUpdate){
                        atualizarRecycler();
                    }
                }else if(resposta instanceof GraphqlError){
                    final GraphqlError error = (GraphqlError) resposta;
                    if(error.getCode()==22){//Nenhuma categoria encontrada
                        DB.removeAllCategoria();
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
