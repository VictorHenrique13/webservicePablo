package br.com.viphost.kardenapp.CONTROLLER.connections.adapter.pedidos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarPedidos;
import br.com.viphost.kardenapp.CONTROLLER.queries.VerificarPermissao;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro;
import br.com.viphost.kardenapp.CONTROLLER.tipos.ListaPedidos;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Pedido;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.MODEL.ItemPedido;
import br.com.viphost.kardenapp.MODEL.ItensPedido;
import br.com.viphost.kardenapp.MODEL.Ponteiro;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.VIEW.MainActivity;

public class ObterPedidos {
    private final Activity ACTIVITY;
    private final int mesa;
    private final ArrayList<ItemPedido> pr;
    private final DbOpenhelper DB;
    private Ponteiro valorTotal;
    private int result = -2;
    public ObterPedidos(Activity activity, int mesa, ArrayList<ItemPedido> pr, Ponteiro valorTotal){
        this.ACTIVITY = activity;
        this.mesa=mesa;
        this.pr=pr;
        this.DB = new DbOpenhelper(this.ACTIVITY);
        this.valorTotal=valorTotal;
        pr.clear();
    }

    private void processar(){
        final String deviceID = new DeviceInfo().getDeviceID(ACTIVITY);
        final GraphqlClient graphqlClient = new GraphqlClient();
        final ListarPedidos listarPedidos = new ListarPedidos(graphqlClient);
        GraphqlResponse resposta = listarPedidos.run(mesa,DB.getToken(),deviceID);
        if(resposta instanceof ListaPedidos){
            ListaPedidos response = (ListaPedidos)resposta;
            Pedido[] pedidos = response.getAll();
            double total = 0;
            for(int x=0; x<pedidos.length;x++){
                br.com.viphost.kardenapp.CONTROLLER.tipos.ItemPedido[] itens = pedidos[x].getItens();
                for(br.com.viphost.kardenapp.CONTROLLER.tipos.ItemPedido item : itens){
                    br.com.viphost.kardenapp.CONTROLLER.tipos.Produto produtoConexao = item.getProduto();
                    Produto produto;
                    if(produtoConexao==null){
                        produto = new Produto(0,"Desconhecido",0,0,"Desconhecida");
                    }else {
                        int idProduto = 0;
                        int quantidade = 0;
                        double valor = 0;
                        if(produtoConexao.getId()!=null){
                            idProduto=produtoConexao.getId();
                        }
                        if(item.getQuantidade()!=null){
                            quantidade=item.getQuantidade();
                        }
                        if(item.getValor()!=null){
                            valor=item.getValor();
                        }
                        produto = new Produto(idProduto, produtoConexao.getNome(), quantidade, valor, "Temporario");
                    }
                    ItemPedido itemPedido = new ItemPedido(x+1,produto);
                    pr.add(itemPedido);
                    total+=produto.getDoublePreco()*produto.getIntQuantidade();
                }
            }
            valorTotal.setValue((Double)total);
            result=1;
        }else if(resposta instanceof GraphqlError){
            result=0;
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
            result=0;
            new Balao(ACTIVITY,"Erro desconhecido",Toast.LENGTH_LONG).show();
        }
        //progressDialog.dismiss();
    }
    public void run(boolean inThread){
        result=-1;
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
    public void run(Thread threadToInterruptOnFinaly){
        result=-1;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                processar();
                threadToInterruptOnFinaly.interrupt();
            }
        };
        new Thread(runnable).start();
    }

    public void run(ProgressDialog progressDialog){
        result=-1;
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
    public void run(ProgressDialog progressDialog,Thread threadToInterruptOnFinaly){
        result=-1;
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                processar();
                threadToInterruptOnFinaly.interrupt();
                progressDialog.dismiss();
            }
        };
        new Thread(runnable).start();
    }

    public int getResult() {
        return result;
    }
}
