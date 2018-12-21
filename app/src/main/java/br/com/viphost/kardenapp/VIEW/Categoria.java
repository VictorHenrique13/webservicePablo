package br.com.viphost.kardenapp.VIEW;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.tipos.ListaCategorias;
import br.com.viphost.kardenapp.CONTROLLER.mutations.CadastrarCategoria;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarCategorias;
import br.com.viphost.kardenapp.CONTROLLER.utils.AtualizarMesas;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.CONTROLLER.utils.BinaryTool;
import br.com.viphost.kardenapp.CONTROLLER.utils.Database;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterWithIcon;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Categoria extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomAppBar bottomAppBar;
    private RecyclerView recyclerView;
    private AlertDialog categoriaD;
    ArrayList<String> f = new ArrayList<String>();
    ArrayList<Integer> f_ids = new ArrayList<>();
    private EditText edtCategoria;
    private Button btnSaveCategoria;
    private FloatingActionButton floatingActionButton;
    private ImageView iconSearch;
    private ImageView carShop;
    private  String texto;
    private DbOpenhelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AtualizarMesas.reference!=null){
            AtualizarMesas.reference.finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);
        toolbar = findViewById(R.id.toolbar);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        floatingActionButton = findViewById(R.id.floatingG);
        recyclerView = findViewById(R.id.recyclerCategoria);
        iconSearch = findViewById(R.id.iconSearch);
        carShop =findViewById(R.id.imgCarrinho);
        setSupportActionBar(toolbar);
        ActionBar t = getSupportActionBar();
        t.setTitle("Categoria");
        t.setDisplayHomeAsUpEnabled(true);
        DB = new DbOpenhelper(this);
        if(BinaryTool.BitValueOfInt(DB.getPermissao(),4)==false){
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams)floatingActionButton.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            p.width = 0;
            p.height = 0;
            floatingActionButton.setLayoutParams(p);
            floatingActionButton.hide();
            //((View) floatingActionButton).setVisibility(View.GONE);
            //Esconder o botao de cadastro
        }
        carShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Categoria.this,Carrinho.class));
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Categoria.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final AdapterWithIcon adp = new AdapterWithIcon(Categoria.this,f);
        recyclerView.setAdapter(adp);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy>0){
                    bottomAppBar.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                }else {
                    bottomAppBar.setVisibility(View.VISIBLE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(Categoria.this);
                View ct = getLayoutInflater().inflate(R.layout.dialog_categoria,null);
                edtCategoria = ct.findViewById(R.id.edtNomeCategoria);
                texto = edtCategoria.getText().toString();
                btnSaveCategoria = ct.findViewById(R.id.btnSalvarCategoriaD);
                btnSaveCategoria.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(edtCategoria.getText().toString().isEmpty()){
                            Toast.makeText(Categoria.this,"Deve digitar número da mesa!",Toast.LENGTH_SHORT).show();
                        }else{
                            //Conexao do cadastrar categoria
                            final String deviceID = new DeviceInfo().getDeviceID(getApplicationContext());
                            final GraphqlClient graphqlClient = new GraphqlClient();
                            final CadastrarCategoria cadastrarMesa = new CadastrarCategoria(graphqlClient);
                            //final ProgressDialog progressDialog= ProgressDialog.show(getApplicationContext(), "Cadastrando...","Cadastrando Mesa, aguarde...", true);
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    GraphqlResponse resposta = cadastrarMesa.run(edtCategoria.getText().toString(),Database.getToken(getApplicationContext()),deviceID);
                                    if(resposta instanceof br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro){
                                        br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro response = (br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro)resposta;
                                        f.add(edtCategoria.getText().toString());
                                        f_ids.add(response.getValor());
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adp.notifyItemInserted(f.lastIndexOf(edtCategoria.getText().toString()));
                                            }
                                        });
                                        new Balao(Categoria.this,"Categoria Cadastrada",Toast.LENGTH_LONG).show();
                                    }else if(resposta instanceof GraphqlError){
                                        final GraphqlError error = (GraphqlError) resposta;
                                        new Balao(Categoria.this, error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_LONG).show();
                                    } else{
                                        new Balao(Categoria.this,"Erro desconhecido",Toast.LENGTH_LONG).show();
                                    }
                                    //progressDialog.dismiss();
                                }
                            };
                            new Thread(runnable).start();
                            ////////////

                            categoriaD.dismiss();
                        }
                    }
                });

                b.setCancelable(true);
                b.setTitle("Categoria");
                b.setView(ct);
                categoriaD = b.create();
                categoriaD.show();
            }
        });
        //Puxar Todas as categorias//////////////////////////////////////////////////////////////////////////////////////
        final String deviceID = new DeviceInfo().getDeviceID(getApplicationContext());
        final GraphqlClient graphqlClient = new GraphqlClient();
        final ListarCategorias listarCategorias = new ListarCategorias(graphqlClient);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GraphqlResponse resposta = listarCategorias.run(Database.getToken(getApplicationContext()),deviceID);
                if(resposta instanceof ListaCategorias){
                    ListaCategorias array =(ListaCategorias) resposta;
                    int count =0;
                    int tamanho = f.size();
                    ArrayList<String> toAddStrings = new ArrayList<>();
                    ArrayList<Integer> toAddInts = new ArrayList<>();
                    while(array.hasNext()){
                        br.com.viphost.kardenapp.CONTROLLER.tipos.Categoria next = array.getNext();
                        if(count<tamanho){
                            if(f.get(count)!=next.getNome()){
                                f.set(count,next.getNome());
                                f_ids.set(count,next.getId());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adp.notifyDataSetChanged();
                                    }
                                });
                            }
                        }else{
                            toAddStrings.add(next.getNome());
                            toAddInts.add(next.getId());
                        }
                        count++;
                    }
                    while(count<tamanho){
                        f.remove(count);
                        final int finalCount = count;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.removeViewAt(finalCount);
                                adp.notifyItemRemoved(finalCount);
                                adp.notifyItemRangeChanged(finalCount, f.size());
                            }
                        });
                        count++;
                    }
                    if(toAddStrings.size()>0){
                        f.addAll(tamanho, toAddStrings);
                        f_ids.addAll(tamanho, toAddInts);
                        final int finalIndex = tamanho;
                        final int finalSize = toAddStrings.size();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adp.notifyItemRangeInserted(finalIndex, finalSize);
                            }
                        });
                    }
                }else if(resposta instanceof GraphqlError){
                    final GraphqlError error = (GraphqlError) resposta;
                    new Balao(Categoria.this, error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_LONG).show();
                } else{
                    new Balao(Categoria.this,"Erro desconhecido",Toast.LENGTH_LONG).show();
                }
            }
        };
        new Thread(runnable).start();
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        ///////////GAMBIARRA/////////
        Database.setCategorias(f,f_ids);
        ////////GAMBIARRA////////////
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
