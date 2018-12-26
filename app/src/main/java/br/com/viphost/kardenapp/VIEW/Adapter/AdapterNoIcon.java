package br.com.viphost.kardenapp.VIEW.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.mutations.AbrirMesa;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Mesa;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.CONTROLLER.utils.Memoria;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Categoria;
import br.com.viphost.kardenapp.VIEW.Holder.ViewH;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterNoIcon extends RecyclerView.Adapter<ViewH> {
    private /*Context*/Activity context;
    private ArrayList<String> mesa;
    private DbOpenhelper DB;

    public AdapterNoIcon(/*Context*/Activity context, ArrayList<String> mesa) {
        this.context = context;
        this.mesa = mesa;
        DB = new DbOpenhelper(context);
    }

    @NonNull
    @Override
    public ViewH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_mesas,parent,false);
        return new ViewH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewH holder, int position) {
        final String mesa_str = mesa.get(position);
        holder.txtTitleMesa.setText(mesa.get(position));
        holder.txtTitleMesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CONEXAO DE ABRIR MESA//////////////////////////////////////////////
                Memoria.setMesaAtual(mesa_str);
                final String deviceID = new DeviceInfo().getDeviceID(context);
                final GraphqlClient graphqlClient = new GraphqlClient();
                final AbrirMesa abrirMesa = new AbrirMesa(graphqlClient);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        GraphqlResponse resposta = abrirMesa.run(DB.getToken(), deviceID,mesa_str);
                        if(resposta instanceof Mesa){
                            Mesa mesa_obj = ((Mesa) resposta);
                            Memoria.setAtualComanda(mesa_str,mesa_obj.getComanda());
                            if(mesa_obj.getJaAberta()){
                                new Balao(context,"Esta mesa ja estava aberta",Toast.LENGTH_SHORT).show();
                            }
                            new Balao(context,"Comanda: "+mesa_obj.getComanda(),Toast.LENGTH_LONG).show();
                        }else if(resposta instanceof GraphqlError){
                            Memoria.setAtualComanda(mesa_str,-1);
                            final GraphqlError error = (GraphqlError) resposta;
                            new Balao(context, error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_LONG).show();
                        } else{
                            Memoria.setAtualComanda(mesa_str,-1);
                            new Balao(context,"Erro desconhecido",Toast.LENGTH_LONG).show();
                        }
                    }
                };
                new Thread(runnable).start();
                //////////////////////////////////////////////
                Intent g = new Intent(context, Categoria.class);
                g.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(g);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mesa.size();
    }
}
