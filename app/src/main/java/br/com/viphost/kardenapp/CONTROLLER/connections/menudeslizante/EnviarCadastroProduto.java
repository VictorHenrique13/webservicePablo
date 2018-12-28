package br.com.viphost.kardenapp.CONTROLLER.connections.menudeslizante;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.mutations.CadastrarProduto;
import br.com.viphost.kardenapp.CONTROLLER.queries.VerificarPermissao;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.VIEW.Categoria;
import br.com.viphost.kardenapp.VIEW.MainActivity;

public class EnviarCadastroProduto {
    private final Activity ACTIVITY;
    private final DbOpenhelper DB;
    private final String nomeProduto;
    private final double valorProduto;
    private final String nomeCategoriaProduto;
    public EnviarCadastroProduto(Activity activity, String nomeProduto, double valorProduto, String nomeCategoriaProduto){
        this.ACTIVITY=activity;
        this.DB = new DbOpenhelper(ACTIVITY);
        this.nomeProduto=nomeProduto;
        this.valorProduto=valorProduto;
        this.nomeCategoriaProduto=nomeCategoriaProduto;
    }

    private void processar(){
        final String deviceID = new DeviceInfo().getDeviceID(ACTIVITY);
        final GraphqlClient graphqlClient = new GraphqlClient();
        final CadastrarProduto cadastrarProduto = new CadastrarProduto(graphqlClient);
        GraphqlResponse resposta = cadastrarProduto.run(nomeProduto,valorProduto,nomeCategoriaProduto,DB.getToken(),deviceID);
        if(resposta instanceof Inteiro){
            Inteiro response = (Inteiro)resposta;
            //Produto produto = new Produto(response.getValor(), nomeProduto, 0, valorProduto, nomeCategoriaProduto);
            //DB.insertProduto(produto);//Adicionando Ofline no banco SQLite
            new Balao(ACTIVITY,"Produto Cadastrado",Toast.LENGTH_SHORT).show();
        }else if(resposta instanceof GraphqlError){
            final GraphqlError error = (GraphqlError) resposta;
            if(error.getCode()==2){//token expirado
                Intent m = new Intent(ACTIVITY, MainActivity.class);
                m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                new Balao(ACTIVITY,"Sessão expirada", Toast.LENGTH_SHORT).show();
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
