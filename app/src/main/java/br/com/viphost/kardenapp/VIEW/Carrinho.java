package br.com.viphost.kardenapp.VIEW;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.connections.mesas.AtualizarPermissao;
import br.com.viphost.kardenapp.CONTROLLER.mutations.AdicionarPedido;
import br.com.viphost.kardenapp.CONTROLLER.mutations.CadastrarMesa;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.CONTROLLER.utils.Database;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterSingleCategoria;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static br.com.viphost.kardenapp.R.drawable.ic_check_black_24dp;

public class Carrinho extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton floatingActionButton;
    private ImageView iconSearch;
    private ImageView menu;
    private ImageView carShop;
    private RecyclerView recyclerView;
    private ArrayList<Produto> prods = new ArrayList<>();
    private AlertDialog confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prods = Database.getCarrinhoMesaAtual().getProdutos();
        setContentView(R.layout.activity_carrinho);
        toolbar = findViewById(R.id.toolbar);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        floatingActionButton = findViewById(R.id.floatingG);
        iconSearch = findViewById(R.id.iconSearch);
        carShop = findViewById(R.id.imgCarrinho);
        recyclerView =findViewById(R.id.recyclerCarrinho);

        new AtualizarPermissao(this).run(true);
        setSupportActionBar(toolbar);
        ActionBar t = getSupportActionBar();
        t.setTitle("Carrinho");
        t.setDisplayHomeAsUpEnabled(true);
        final AdapterSingleCategoria adp = new AdapterSingleCategoria(Carrinho.this,prods);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Carrinho.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adp);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Conexao de manda Pedido
                final String deviceID = new DeviceInfo().getDeviceID(getApplicationContext());
                final GraphqlClient graphqlClient = new GraphqlClient();
                final AdicionarPedido adicionarPedido = new AdicionarPedido(graphqlClient);
                final String[][] itens = new String[prods.size()][];//["idproduto","quantidade","anotacoes","acrescimo/desconto"]
                int count = 0;
                for(Produto produto : prods){

                    itens[count] = new String[]{produto.getId()+"",produto.getIntQuantidade()+"","",""};
                    count++;
                }
                final ProgressDialog progressDialog= ProgressDialog.show(Carrinho.this, "Enviando...","Enviando Pedido, aguarde...", true);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        GraphqlResponse resposta = adicionarPedido.run(Database.getAtualComanda(Database.getMesaAtual()),itens,Database.getToken(getApplicationContext()),deviceID);
                        if(resposta instanceof br.com.viphost.kardenapp.CONTROLLER.tipos.Logico){
                            br.com.viphost.kardenapp.CONTROLLER.tipos.Logico response = (br.com.viphost.kardenapp.CONTROLLER.tipos.Logico)resposta;
                            if(response.getValor()){
                                new Balao(Carrinho.this,"Pedido Realizado",Toast.LENGTH_SHORT).show();
                                Intent m = new Intent(Carrinho.this,Mesas.class);
                                m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(m);
                                Carrinho.this.finish();
                                Database.getCarrinho(Database.getMesaAtual()).getProdutos().clear();
                                Database.apagarCarrinhoMesa(Database.getMesaAtual());
                                Database.setMesaAtual("-1");
                            }else{
                                new Balao(Carrinho.this,"Nao foi possivel realizar pedido",Toast.LENGTH_LONG).show();
                            }
                        }else if(resposta instanceof GraphqlError){
                            final GraphqlError error = (GraphqlError) resposta;
                            new Balao(Carrinho.this, error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_LONG).show();
                        } else{
                            new Balao(Carrinho.this,"Erro desconhecido",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                };
                new Thread(runnable).start();
                ////////////
                //Toast.makeText(getApplicationContext(),"Pedido Realizado",Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(Carrinho.this,Mesas.class));
                //finish();
            }
        });


    }

    @Override
    protected void onStart() {
        floatingActionButton.setImageResource(R.drawable.ic_check_black_24dp);

        super.onStart();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Carrinho.this,Mesas.class));
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:

                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
