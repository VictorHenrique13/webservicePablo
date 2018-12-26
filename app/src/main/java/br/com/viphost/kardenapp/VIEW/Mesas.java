package br.com.viphost.kardenapp.VIEW;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.connections.mesas.AtualizarPermissao;
import br.com.viphost.kardenapp.CONTROLLER.mutations.CadastrarMesa;
import br.com.viphost.kardenapp.CONTROLLER.mutations.Logout;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Logico;

import br.com.viphost.kardenapp.CONTROLLER.utils.AtualizarMesas;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.CONTROLLER.utils.BinaryTool;
import br.com.viphost.kardenapp.CONTROLLER.utils.Memoria;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterNoIcon;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class Mesas extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomAppBar bottomBar;
    private AlertDialog dialogMesa;
    private ImageView iconSearch;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ArrayList<String> ms=new ArrayList<String>();
    private EditText edtMesas;
    private Button btnSave;
    private AlertDialog close;
    private ImageView carShop;
    private ImageView menuUp;
    private AtualizarMesas atualizarMesas;
    private AdapterNoIcon adp;
    private BottomSheetDialog bottomSheetDialog;
    private Spinner spinner;
    private String nomeCategoria;
    private LinearLayout btnCadastrarProduto;
    private AlertDialog alertCadastroProduto;
    private String[] categorias = {"Categorias","fera","loko"};
    //referencias da dialog Cadasreo produto
    private TextInputLayout layNomeProdCad;
    private TextInputLayout layPrecoCad;
    private TextInputLayout layQuantidadeCad;
    private TextInputEditText edtNomeProdCad;
    private TextInputEditText edtQuantidadeProdCad;
    private TextInputEditText edtPrecoProdCad;
    private TextView btnConfirmCad;
    private TextView btnCancelCad;
    //----------------------------
    private DbOpenhelper DB;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);
        toolbar = findViewById(R.id.toolbar);
        bottomBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        iconSearch = findViewById(R.id.iconSearch);
        final View bottomB = getLayoutInflater().inflate(R.layout.bottom_behavior,null);
        recyclerView = findViewById(R.id.recyclerMesas);
        floatingActionButton = findViewById(R.id.floatingG);
        carShop = findViewById(R.id.imgCarrinho);
        DB = new DbOpenhelper(this);
        setSupportActionBar(toolbar);
        ActionBar t = getSupportActionBar();
        t.setTitle("Mesa");
        t.setDisplayHomeAsUpEnabled(true);
        carShop.setVisibility(View.GONE);
        //floatingActionButton.hide();
        //menu deslizante para cima
        bottomSheetDialog = new BottomSheetDialog(Mesas.this,R.style.BottomSheetDialog);
        View modal = getLayoutInflater().inflate(R.layout.bottom_behavior,null);
        bottomSheetDialog.setContentView(modal);
        menuUp = findViewById(R.id.menuUp);

        new AtualizarPermissao(this).run(true);
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
        menuUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });
        //funçoes menu deslizante
        btnCadastrarProduto = modal.findViewById(R.id.cadastrarProdutoAction);
        btnCadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(Mesas.this);
                View cadastroDialog = LayoutInflater.from(Mesas.this).inflate(R.layout.cadastro_produto,null);
                edtNomeProdCad = cadastroDialog.findViewById(R.id.edtNomeProdCad);
                edtPrecoProdCad = cadastroDialog.findViewById(R.id.edtPrecoCad);
                layNomeProdCad = cadastroDialog.findViewById(R.id.layNomeProdCad);
                layPrecoCad = cadastroDialog.findViewById(R.id.layPrecoCad);
                btnConfirmCad = cadastroDialog.findViewById(R.id.btnConfirmCad);
                btnCancelCad = cadastroDialog.findViewById(R.id.btnCancelCad);
                spinner = cadastroDialog.findViewById(R.id.spinner);
                ArrayAdapter sp = new ArrayAdapter(Mesas.this,R.layout.support_simple_spinner_dropdown_item,categorias);
                sp.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(sp);
                //dados para envio em formato usavel
                //caso algum tipo de variavel esta errado so realizar troca

                String nomeProdutoCadastro = edtNomeProdCad.getText().toString();
                String precoProdutoCAdastro = edtPrecoProdCad.getText().toString();

                //-----------------------------------------------------------------------------
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            nomeCategoria = (String) parent.getItemAtPosition(position);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(Mesas.this,"Escolha uma opção válida",Toast.LENGTH_SHORT).show();
                    }
                });
                btnConfirmCad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(validateCadastroProd()){
                            //conexao server aqui


                            //-------------------------------
                            //conexao offline aqui-------



                            //-----------------------------

                            edtPrecoProdCad.setText("");
                            edtNomeProdCad.setText("");
                            edtNomeProdCad.findFocus();
                            alertCadastroProduto.dismiss();
                        }else{
                            Toast.makeText(Mesas.this,"Deve digitar algum valor!",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                btnCancelCad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertCadastroProduto.dismiss();
                    }
                });

                    b.setTitle("Cadastro Produto");
                    b.setView(cadastroDialog);
                    alertCadastroProduto = b.create();
                    alertCadastroProduto.show();
            }
        });

        //--------------------------------------
        //--------------------------------------------------
        //inicio floating button
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(Mesas.this);
                View msa = getLayoutInflater().inflate(R.layout.dialog_mesa,null);
                edtMesas = msa.findViewById(R.id.numMesaTxtD);
                btnSave = msa.findViewById(R.id.btnSalvarMesaD);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(edtMesas.getText().toString().isEmpty()){
                            Toast.makeText(Mesas.this,"Deve digitar número da mesa!",Toast.LENGTH_SHORT).show();
                        }else{
                            //Conexao do cadastrar mesa
                            final String deviceID = new DeviceInfo().getDeviceID(getApplicationContext());
                            final GraphqlClient graphqlClient = new GraphqlClient();
                            final CadastrarMesa cadastrarMesa = new CadastrarMesa(graphqlClient);
                            //final ProgressDialog progressDialog= ProgressDialog.show(getApplicationContext(), "Cadastrando...","Cadastrando Mesa, aguarde...", true);
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    GraphqlResponse resposta = cadastrarMesa.run(edtMesas.getText().toString(), DB.getToken(),deviceID);
                                    if(resposta instanceof Logico){
                                        Logico response = (Logico)resposta;
                                        if(response.getValor()){
                                            ms.add(edtMesas.getText().toString());
                                            new Balao(Mesas.this,"Mesa Cadastrada",Toast.LENGTH_LONG).show();
                                        }else{
                                            new Balao(Mesas.this,"Nao foi possivel cadastrar mesa",Toast.LENGTH_LONG).show();
                                        }
                                    }else if(resposta instanceof GraphqlError){
                                        final GraphqlError error = (GraphqlError) resposta;
                                        new Balao(Mesas.this, error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_LONG).show();
                                    } else{
                                        new Balao(Mesas.this,"Erro desconhecido",Toast.LENGTH_LONG).show();
                                    }
                                    //progressDialog.dismiss();
                                }
                            };
                            new Thread(runnable).start();
                            ////////////
                            dialogMesa.dismiss();

                        }
                    }
                });

                b.setView(msa);
                b.setTitle("Mesas");
                dialogMesa = b.create();
                dialogMesa.show();


            }
        });
        //fim flaoting button
        //recycler view inicio

        AdapterNoIcon adp = new AdapterNoIcon(Mesas.this,ms);
        RecyclerView.LayoutManager mg = new LinearLayoutManager(Mesas.this);
        recyclerView.setLayoutManager(mg);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adp);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy>0){
                    bottomBar.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);
                }else{
                    bottomBar.setVisibility(View.VISIBLE);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        atualizarMesas = new AtualizarMesas(this,ms,recyclerView,adp);
        atualizarMesas.start();
        AtualizarMesas.reference=atualizarMesas;
        //fim recycler view
    }

    private boolean validateCadastroProd() {
        return true;
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {

        AlertDialog.Builder b = new AlertDialog.Builder(Mesas.this);
        b.setMessage("Deseja realmente sair da sessão");
        b.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //logout  é realizado aqui
                final GraphqlClient graphqlClient = new GraphqlClient();
                final String deviceID = new DeviceInfo().getDeviceID(getApplicationContext());
                GraphqlResponse response = new Logout(graphqlClient).run(DB.getToken(),deviceID);
                if(response instanceof GraphqlError){
                    GraphqlError error = (GraphqlError)response;
                    Toast.makeText(getApplicationContext(), error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_SHORT).show();
                }
                //Memoria.setToken(getApplicationContext(),"");
                DB.deleteLogin();
                Intent m = new Intent(getApplicationContext(),MainActivity.class);
                m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                close.dismiss();
                //----------------------------
                atualizarMesas.finish();
                startActivity(m);
                finish();
            }
        });
        b.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                close.dismiss();
            }
        });
        b.setCancelable(true);//Destravei puq acho chato app que me impeça de usar o back :p
        //Tenta implementar aquele negocio de 3 backs pra fechar que fica melhor
        close = b.create();
        close.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                atualizarMesas.finish();
                close.dismiss();
                finish();
            }
        });
        close.show();
        //super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:

                AlertDialog.Builder b = new AlertDialog.Builder(Mesas.this);
                b.setMessage("Deseja realmente sair da sessão");
                b.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //logout  é realizado aqui
                        final GraphqlClient graphqlClient = new GraphqlClient();
                        final String deviceID = new DeviceInfo().getDeviceID(getApplicationContext());
                        GraphqlResponse response = new Logout(graphqlClient).run(DB.getToken(),deviceID);
                        if(response instanceof GraphqlError){
                            GraphqlError error = (GraphqlError)response;
                            Toast.makeText(getApplicationContext(), error.getMessage() + ". " + error.getCategory() + "[" + error.getCode() + "]", Toast.LENGTH_SHORT).show();
                        }
                        //Memoria.setToken(getApplicationContext(),"");
                        DB.deleteLogin();
                        Intent m = new Intent(getApplicationContext(),MainActivity.class);
                        m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        close.dismiss();
                        //----------------------------
                        atualizarMesas.finish();
                        startActivity(m);
                        finish();
                    }
                });
                b.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        close.dismiss();
                    }
                });
                b.setCancelable(false);
                close = b.create();
                close.show();
                break;
        }
        return true;
    }
}
