package br.com.viphost.kardenapp.VIEW;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import br.com.viphost.kardenapp.CONTROLLER.tipos.ListaProdutos;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarProdutos;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.CONTROLLER.utils.Database;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterSingleCategoria;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class PageCategoria extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomAppBar bottomAppBar;
    private ImageView iconSearch;
    private ImageView carShop;
    private ImageView menu;
    private RecyclerView recyclerView;
    private ArrayList<Produto> prod = new ArrayList<Produto>();
    private Produto produto;
    private BottomSheetDialog bottomSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_categoria);
        toolbar = findViewById(R.id.toolbar);
        bottomAppBar = findViewById(R.id.bottomProdutoSingle);
        iconSearch = findViewById(R.id.searchProdutoSingle);
        carShop = findViewById(R.id.carShopProdutoSingle);
        recyclerView = findViewById(R.id.recyclerPageCategoria);
        menu = findViewById(R.id.menuProdutoSingle);
        bottomSheetDialog = new BottomSheetDialog(PageCategoria.this);
        View modal = getLayoutInflater().inflate(R.layout.bottom_behavior,null);
        bottomSheetDialog.setContentView(modal);
        carShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PageCategoria.this,Carrinho.class));
            }
        });
        setSupportActionBar(toolbar);
        ActionBar t = getSupportActionBar();
        //recuperando informações da tela anterior
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        final String info = args.getString("Nome");
        //--------------------------------------------
        t.setTitle(info);
        t.setDisplayHomeAsUpEnabled(true);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });
        new AtualizarPermissao(this).run(true);
        final AdapterSingleCategoria adp = new AdapterSingleCategoria(PageCategoria.this,prod);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PageCategoria.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adp);

        //Puxar Todas os Produtos//////////////////////////////////////////////////////////////////////////////////////
        final String deviceID = new DeviceInfo().getDeviceID(getApplicationContext());
        final GraphqlClient graphqlClient = new GraphqlClient();
        final ListarProdutos listarProdutos = new ListarProdutos(graphqlClient);
        final int idCategoria = Database.getIdFromCategoria(info);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GraphqlResponse resposta = listarProdutos.run(idCategoria,Database.getToken(getApplicationContext()),deviceID);
                if(resposta instanceof ListaProdutos){
                    ListaProdutos array =(ListaProdutos) resposta;
                    int count =0;
                    int tamanho = prod.size();
                    ArrayList<Produto> toAdd = new ArrayList<>();
                    while(array.hasNext()){
                        br.com.viphost.kardenapp.CONTROLLER.tipos.Produto next = array.getNext();
                        if(count<tamanho){
                            if(prod.get(count).getId()!=next.getId()){
                                int quantidade = 0;///VERIFICAR SE EXISTE QUANTIDADE DESSE ITEM NO CARRINHO
                                Produto novo_produto = new Produto(next.getId(),next.getNome(),quantidade,next.getValor(),info);
                                prod.set(count,novo_produto);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adp.notifyDataSetChanged();
                                    }
                                });
                            }
                        }else{
                            int quantidade = 0;///VERIFICAR SE EXISTE QUANTIDADE DESSE ITEM NO CARRINHO
                            Produto novo_produto = new Produto(next.getId(),next.getNome(),quantidade,next.getValor());
                            toAdd.add(novo_produto);
                        }
                        count++;
                    }
                    while(count<tamanho){
                        prod.remove(count);
                        final int finalCount = count;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.removeViewAt(finalCount);
                                adp.notifyItemRemoved(finalCount);
                                adp.notifyItemRangeChanged(finalCount, prod.size());
                            }
                        });
                        count++;
                    }
                    if(toAdd.size()>0){
                        prod.addAll(tamanho, toAdd);
                        final int finalIndex = tamanho;
                        final int finalSize = toAdd.size();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adp.notifyItemRangeInserted(finalIndex, finalSize);
                            }
                        });
                    }
                }else if(resposta instanceof GraphqlError){
                    final GraphqlError error = (GraphqlError) resposta;
                    new Balao(PageCategoria.this, error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_LONG).show();
                } else{
                    new Balao(PageCategoria.this,"Erro desconhecido",Toast.LENGTH_LONG).show();
                }
            }
        };
        new Thread(runnable).start();
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    @Override
    public void onBackPressed() {

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
