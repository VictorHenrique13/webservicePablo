package br.com.viphost.kardenapp.CONTROLLER.connections.mesas;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarMesas;
import br.com.viphost.kardenapp.CONTROLLER.tipos.IntArray;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterNoIcon;

public class AtualizarMesas {
    public static AtualizarMesas reference = null;
    private RecyclerView recyclerView;
    private ArrayList<String> ms;
    private Activity activity;
    private AdapterNoIcon adp;
    private DbOpenhelper DB;
    Timer timer;

    public AtualizarMesas(Activity activity, ArrayList<String> ms, RecyclerView recyclerView, AdapterNoIcon adp){
        this.recyclerView=recyclerView;
        this.ms=ms;
        this.activity=activity;
        this.adp=adp;
        DB = new DbOpenhelper(activity);
        timer = new Timer();
    }
    private void atualizarRecycler(){
        ArrayList<String> array = DB.getListaMesas();
        ArrayList<String> toAdd = new ArrayList<>();
        int count =0;
        int tamanho = ms.size();
        int notifyRemove = -1;
        for(String nomeMesa : array){
            if(count<tamanho){
                if(ms.get(count)!=nomeMesa){
                    ms.set(count,nomeMesa);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adp.notifyDataSetChanged();
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
            ms.remove(index);
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
            ms.addAll(tamanho, toAdd);
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
    public void start(){
        atualizarRecycler();
        final GraphqlClient graphqlClient = new GraphqlClient();
        graphqlClient.setDebugger(false);
        final ListarMesas listarMesas = new ListarMesas(graphqlClient);
        final String deviceID = new DeviceInfo().getDeviceID(this.activity.getApplicationContext());
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                GraphqlResponse resposta = listarMesas.run(DB.getToken(),deviceID);
                if(resposta instanceof IntArray){
                    boolean needUpdate = false;
                    IntArray array =(IntArray) resposta;
                    ArrayList<String> toAdd = new ArrayList<>();
                    ArrayList<String> DbArrayMesas = DB.getListaMesas();
                    int count =0;
                    int tamanho = DbArrayMesas.size();
                    while(array.hasNext()){
                        String next = array.getNext()+"";
                        if(count<tamanho){
                            String mesaDb = DbArrayMesas.get(count);
                            if(mesaDb!=next){
                                DB.updateMesa(mesaDb,next);//DbArrayMesas.set(count,next);
                                needUpdate=true;
                            }
                        }else{
                            toAdd.add(next);
                        }
                        count++;
                    }
                    while(count<tamanho){
                        String mesaDb = DbArrayMesas.get(count);
                        DB.removeMesa(mesaDb);//DbArrayMesas.remove(count);
                        needUpdate=true;
                        count++;
                    }
                    if(toAdd.size()>0){
                        needUpdate=true;
                        for(String mesaToAdd : toAdd) {//DbArrayMesas.addAll(tamanho, toAdd);
                            DB.insertMesa(mesaToAdd);
                        }
                    }
                    if(needUpdate){
                        atualizarRecycler();
                    }
                }else if(resposta instanceof GraphqlError){
                    final GraphqlError error = (GraphqlError) resposta;
                    if(error.getCode()==14){//Nenhuma mesa encontrada
                        DB.removeAllMesa();
                        atualizarRecycler();
                    }
                    System.out.println( error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]");
                } else{
                    System.out.println("Erro desconhecido no AtualizarMesas");
                }
            }
        };
        timer.schedule(timerTask,0,1000);
    }
    public void finish(){
        timer.cancel();
        timer.purge();
    }
    public ArrayList<String> get(){
        final GraphqlClient graphqlClient = new GraphqlClient();
        final ListarMesas listarMesas = new ListarMesas(graphqlClient);
        final String deviceID = new DeviceInfo().getDeviceID(this.activity.getApplicationContext());
        GraphqlResponse resposta = listarMesas.run(DB.getToken(),deviceID);
        ArrayList<String> retorno = new ArrayList<String>();
        if(resposta instanceof IntArray){
            IntArray array =(IntArray) resposta;
            retorno.clear();
            while(array.hasNext()){
                retorno.add(""+array.getNext());
            }
        }else if(resposta instanceof GraphqlError){
            final GraphqlError error = (GraphqlError) resposta;
            System.out.println( error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]");
        } else{
            System.out.println("Erro desconhecido no AtualizarMesas");
        }
        return retorno;
    }
}
