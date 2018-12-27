package br.com.viphost.kardenapp.CONTROLLER.connections;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.queries.VerificarPermissao;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.VIEW.MainActivity;

public class AtualizarPermissao {
    private final Activity ACTIVITY;
    private final DbOpenhelper DB;
    public AtualizarPermissao(Activity activity){
        this.ACTIVITY = activity;
        this.DB = new DbOpenhelper(this.ACTIVITY);
    }

    private void processar(){
        final String deviceID = new DeviceInfo().getDeviceID(ACTIVITY);
        final GraphqlClient graphqlClient = new GraphqlClient();
        final VerificarPermissao verificarPermissao = new VerificarPermissao(graphqlClient);
        GraphqlResponse resposta = verificarPermissao.run(DB.getToken(),deviceID);
        if(resposta instanceof Inteiro){
            Inteiro response = (Inteiro)resposta;
            if(DB.getPermissao()!=response.getValor()){
                DB.setPermissao(response.getValor());
            }
        }else if(resposta instanceof GraphqlError){
            final GraphqlError error = (GraphqlError) resposta;
            if(error.getCode()==2){//token expirado
                Intent m = new Intent(ACTIVITY,MainActivity.class);
                m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                new Balao(ACTIVITY,"Sessão expirada",Toast.LENGTH_SHORT).show();
                ACTIVITY.startActivity(m);
                ACTIVITY.finish();
            }else if(error.getCode()==3){//token invalido para dispositivo
                Intent m = new Intent(ACTIVITY,MainActivity.class);
                m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                new Balao(ACTIVITY,"Necessario autenticar novamente",Toast.LENGTH_SHORT).show();
                ACTIVITY.startActivity(m);
                ACTIVITY.finish();
            }else{
                new Balao(ACTIVITY, error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_LONG).show();
            }
        } else{
            new Balao(ACTIVITY,"Erro desconhecido",Toast.LENGTH_LONG).show();
        }
        //progressDialog.dismiss();
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
