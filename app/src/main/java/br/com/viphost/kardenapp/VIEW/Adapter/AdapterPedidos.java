package br.com.viphost.kardenapp.VIEW.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.CONTROLLER.connections.adapter.pedidos.ObterPedidos;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.MODEL.ItemPedido;
import br.com.viphost.kardenapp.MODEL.Ponteiro;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Holder.ViewH;

public class AdapterPedidos extends RecyclerView.Adapter<ViewH> {

    private Activity context;
    private ArrayList<String> pedidos;
    private AlertDialog dialogDelete;
    private ArrayList<ItemPedido> pr = new ArrayList<>();
    public AdapterPedidos(Activity context, ArrayList<String> pedidos) {
        this.context = context;
        this.pedidos = pedidos;//mesas nao? pelo que entendi na primeira linha do onBindViewHolder
        // preencher pr com os dados de pedidos do server ou aqki ou la no click que pega qual mesa é e ja pega so os valores de itens daquela mesa vai ao seu criterio erlson
    }


    @NonNull
    @Override
    public ViewH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.list_mesas,parent,false);
        return new ViewH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewH holder, int position) {
        holder.txtTitleMesa.setText(pedidos.get(position));
        holder.txtTitleMesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String totalStr = "Total não calculado";
                int numeroMesa;
                try{
                    numeroMesa= Integer.parseInt(pedidos.get(position));
                }catch (Exception e){
                    new Balao(context,"Numero da mesa incorreto", Toast.LENGTH_SHORT);
                    return;
                }
                ///////Conexao começa aq///
                Ponteiro valorTotal = new Ponteiro("Total não calculado");
                ObterPedidos obterPedidos = new ObterPedidos(context,numeroMesa,pr,valorTotal);
                //ProgressDialog progressDialog = ProgressDialog.show(context, "Carregando pedidos...","Carregando pedidos, aguarde...",true,false);

                obterPedidos.run(false);

                //obterPedidos.esperarTerminar(10000);
                int result = obterPedidos.gerResult();
                if(result==1){
                    if(valorTotal.getValue() instanceof Double){
                        Double valor = (Double) valorTotal.getValue();
                        totalStr = String.format("R$ %.2f", valor);
                    }
                }else if(result==0){
                    new Balao(context,"Não carregado", Toast.LENGTH_SHORT);
                    return;
                }else{//Erro na conexao, ela ja vai gerar o seu propio Toast
                    System.out.println("erro para puxar pedidos");
                    return;
                }
                //Termina aq
                //if error { return;}

                ///Daq pra baixo vai ser tudo ignorado se der erro na conexao
                System.out.println("passou");
                AlertDialog.Builder b = new AlertDialog.Builder(context);
                View dp = LayoutInflater.from(context).inflate(R.layout.dialog_inf,null);
                TextView txtTitleItemPedido = dp.findViewById(R.id.txtTitleItensPedidos);
                TextView txtTotalComanda = dp.findViewById(R.id.txtTotalPedidos);
                RecyclerView recyclerView = dp.findViewById(R.id.recyclerItensPedido);
                RecyclerView.LayoutManager ln = new LinearLayoutManager(context);
                AdapterItensPedido adp = new AdapterItensPedido(context,pr);
                recyclerView.setLayoutManager(ln);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adp);
                txtTotalComanda.setText(totalStr);
                txtTitleItemPedido.setText(pedidos.get(position));


                b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogDelete.dismiss();

                    }
                });
                b.setNegativeButton("Deletar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //codigo que deleta pedido dos bancos off e online

                    }
                });

                b.setView(dp);
                dialogDelete = b.create();
                dialogDelete.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }
}
