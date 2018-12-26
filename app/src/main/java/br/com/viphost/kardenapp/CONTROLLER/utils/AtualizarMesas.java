package br.com.viphost.kardenapp.CONTROLLER.utils;

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

    public void start(){
        final GraphqlClient graphqlClient = new GraphqlClient();
        final ListarMesas listarMesas = new ListarMesas(graphqlClient);
        final String deviceID = new DeviceInfo().getDeviceID(this.activity.getApplicationContext());
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                GraphqlResponse resposta = listarMesas.run(DB.getToken(),deviceID);
                if(resposta instanceof IntArray){
                    IntArray array =(IntArray) resposta;
                    int count =0;
                    int tamanho = ms.size();
                    ArrayList<String> toAdd = new ArrayList<>();
                    while(array.hasNext()){
                        String next = array.getNext()+"";
                        if(count<tamanho){
                            if(ms.get(count)!=next){
                                ms.set(count,next);
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adp.notifyDataSetChanged();
                                    }
                                });
                            }
                        }else{
                            toAdd.add(next);
                        }
                        count++;
                    }
                    while(count<tamanho){
                        ms.remove(count);
                        final int finalCount = count;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.removeViewAt(finalCount);
                                adp.notifyItemRemoved(finalCount);
                                adp.notifyItemRangeChanged(finalCount, ms.size());
                            }
                        });
                        count++;
                    }
                    if(toAdd.size()>0){
                        ms.addAll(tamanho, toAdd);
                        final int finalIndex = tamanho;
                        final int finalSize = toAdd.size();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adp.notifyItemRangeInserted(finalIndex, finalSize);
                            }
                        });
                    }
                }else if(resposta instanceof GraphqlError){
                    final GraphqlError error = (GraphqlError) resposta;
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
