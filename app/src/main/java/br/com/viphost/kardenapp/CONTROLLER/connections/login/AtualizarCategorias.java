package br.com.viphost.kardenapp.CONTROLLER.connections.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarCategorias;
import br.com.viphost.kardenapp.CONTROLLER.queries.VerificarPermissao;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Categoria;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro;
import br.com.viphost.kardenapp.CONTROLLER.tipos.ListaCategorias;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.VIEW.MainActivity;

public class AtualizarCategorias {

    private final Activity ACTIVITY;
    private final DbOpenhelper DB;
    public AtualizarCategorias(Activity activity){
        this.ACTIVITY = activity;
        this.DB = new DbOpenhelper(this.ACTIVITY);
    }

    private void processar(){
        final String deviceID = new DeviceInfo().getDeviceID(ACTIVITY);
        final GraphqlClient graphqlClient = new GraphqlClient();
        GraphqlResponse resposta = new ListarCategorias(graphqlClient).run(DB.getToken(),deviceID);
        if(resposta instanceof ListaCategorias){
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
                    }
                }else{
                    toAdd.add(next.getNome());
                }
                count++;
            }
            while(count<tamanho){
                String categoriaDb = DbArrayCategorias.get(count);
                DB.removeCategoria(categoriaDb);//DbArrayCategorias.remove(count);
                count++;
            }
            if(toAdd.size()>0){
                for(String categoriaToAdd : toAdd) {//DbArrayCategorias.addAll(tamanho, toAddStrings);
                    DB.insertCategoria(categoriaToAdd);
                }
            }
        }else if(resposta instanceof GraphqlError){
            final GraphqlError error = (GraphqlError) resposta;
            if(error.getCode()==22){//Nenhuma categoria encontrada
                DB.removeAllCategoria();
                DB.insertCategoria("Nenhuma categoria cadastrada");
            }
            //new Balao(ACTIVITY, error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_LONG).show();
        } else{
            new Balao(ACTIVITY,"Erro desconhecido",Toast.LENGTH_LONG).show();
        }
    }
    public void run(boolean inThread){
        if(inThread){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    processar();
                }
            };
            new Thread(runnable).start();
        }else{
            processar();
        }
    }

    public void run(ProgressDialog progressDialog){
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                processar();
                progressDialog.dismiss();
            }
        };
        new Thread(runnable).start();
    }
}
