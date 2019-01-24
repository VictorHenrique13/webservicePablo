package br.com.viphost.kardenapp.VIEW;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import br.com.viphost.kardenapp.CONTROLLER.connections.AtualizarPermissao;
import br.com.viphost.kardenapp.CONTROLLER.connections.menudeslizante.EnviarCadastroProduto;
import br.com.viphost.kardenapp.CONTROLLER.listeners.mesas.SearchViewListener;
import br.com.viphost.kardenapp.CONTROLLER.mutations.CadastrarMesa;
import br.com.viphost.kardenapp.CONTROLLER.mutations.Logout;
import br.com.viphost.kardenapp.CONTROLLER.tipos.Logico;

import br.com.viphost.kardenapp.CONTROLLER.connections.mesas.AtualizarMesas;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.CONTROLLER.utils.BinaryTool;
import br.com.viphost.kardenapp.MODEL.DadosPessoais;
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
    private ActionBar t;
    private BottomAppBar bottomBar;
    private AlertDialog dialogMesa;
    private ImageView iconSearch;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ArrayList<String> ms=new ArrayList<String>();
    private EditText edtMesas;
    private Button btnSave;
    private AlertDialog close;
    int clickG=0;
    private SearchView searchView;
    private RecyclerView searchRecyclerView;
     private ImageView carShop;
    private ImageView menuUp;
    private AtualizarMesas atualizarMesas;
    //private AdapterNoIcon adp;
    private BottomSheetDialog bottomSheetDialog;
    private TextView NomeSliding;
    private TextView EmailSliding;
    private Spinner spinner;
    private String nomeCategoria;
    private LinearLayout btnCadastrarProduto;
    private LinearLayout btnIrParaPedidos;
    private AlertDialog alertCadastroProduto;
    //private String[] categorias = {"Categorias"};//Preciso recriar dentro do ClickListener pois nao e possivel fixar um tamanho antes
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
        t = getSupportActionBar();
        t.setTitle("Mesa");

        t.setDisplayHomeAsUpEnabled(true);
        carShop.setVisibility(View.GONE);
        ////Search////////////////////////////////////////
        searchView = findViewById(R.id.searchT);
        searchRecyclerView = findViewById(R.id.recyclerSearchMesas);
        ArrayList<String> searchArray = new ArrayList<>();
        searchArray = DB.getListaMesas();
        AdapterNoIcon searchAdp = new AdapterNoIcon(Mesas.this, searchArray);
        SearchViewListener searchViewListener = new SearchViewListener(Mesas.this,searchAdp,searchArray);
        searchView.setOnQueryTextListener(searchViewListener);

        searchRecyclerView.setAdapter(searchAdp);
        RecyclerView.LayoutManager searchLayoutMananger = new LinearLayoutManager(Mesas.this);
        searchRecyclerView.setLayoutManager(searchLayoutMananger);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        ////Search//////////////Fim//////////////////////////
//        iconSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                t.setDisplayShowTitleEnabled(false);
//                searchView.setVisibility(View.VISIBLE);
//            }
//        });

        //floatingActionButton.hide();
        //menu deslizante para cima
        bottomSheetDialog = new BottomSheetDialog(Mesas.this,R.style.BottomSheetDialog);

        View modal = getLayoutInflater().inflate(R.layout.bottom_behavior,null);
        bottomSheetDialog.setContentView(modal);
        menuUp = findViewById(R.id.menuUp);
        NomeSliding = modal.findViewById(R.id.NomeSliding);
        EmailSliding = modal.findViewById(R.id.EmailSliding);
        DadosPessoais dadosPessoais = DB.getDadosPessoais();
        NomeSliding.setText(dadosPessoais.getNome());
        EmailSliding.setText(dadosPessoais.getEmail());

        new AtualizarPermissao(this).run(true);
        if(BinaryTool.BitValueOfInt(DB.getPermissao(),5)==false){
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
        btnIrParaPedidos = modal.findViewById(R.id.irParaPedidosAction);
        if(BinaryTool.BitValueOfInt(DB.getPermissao(),7)==false){
            btnCadastrarProduto.setVisibility(View.GONE);
        }
        btnIrParaPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(getApplicationContext(),Pedidos.class);
                startActivity(m);
            }
        });
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickG==0){
                    t.setDisplayShowTitleEnabled(false);
                    //recyclerViewSearch.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.VISIBLE);
                    searchView.setIconified(false);
                    searchView.setActivated(false);
                    recyclerView.setVisibility(View.GONE);
                    searchRecyclerView.setVisibility(View.VISIBLE);
                    searchViewListener.updateOnShow();
                    clickG++;
                }else{
                    clickG--;
                    searchView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    searchRecyclerView.setVisibility(View.GONE);
                    //recyclerViewSearch.setVisibility(View.GONE);
                    t.setDisplayShowTitleEnabled(true);
                }
            }
        });
        btnCadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(Mesas.this);
                View cadastroDialog = LayoutInflater.from(Mesas.this).inflate(R.layout.cadastro_produto,null);
                ArrayList<String> categoriasList = DB.getListaCategoria();
                String[] categorias = new String[categoriasList.size()];
                categorias = categoriasList.toArray(categorias);

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

                //Desatualizado, vou puxar novamente dentro do click Listener, String é considerado primitivo em java entao isto nao é um ponteiro
                //String nomeProdutoCadastro = edtNomeProdCad.getText().toString();
                //String precoProdutoCAdastro = edtPrecoProdCad.getText().toString();

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
                            String nomeProdutoCadastro = edtNomeProdCad.getText().toString();
                            String precoProdutoCadastroStr = edtPrecoProdCad.getText().toString();
                            double precoProdutoCadastro;
                            try{
                                precoProdutoCadastro = Double.parseDouble(precoProdutoCadastroStr);
                            }catch(Exception e){
                                new Balao(Mesas.this, "Insira um valor no formato correto Ex.: 000.00", Toast.LENGTH_SHORT);
                                return;
                            }
                            //conexao server aqui--------------------//
                            new EnviarCadastroProduto(Mesas.this,nomeProdutoCadastro,precoProdutoCadastro,nomeCategoria).run(true);
                            //---------------------------------------//
                            //conexao offline aqui-------------------//
                            //? vou inserir no SQLite na classe acima//
                            //---------------------------------------//
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
        /*/recycler view inicio
        if(BinaryTool.BitValueOfInt(DB.getPermissao(),4)==false&&BinaryTool.BitValueOfInt(DB.getPermissao(),2)==true){
            AdapterPedidos adp = new AdapterPedidos(Mesas.this,ms);
            recyclerView.setAdapter(adp);
            atualizarMesas = new AtualizarMesas(this,ms,recyclerView,adp);
            atualizarMesas.start(true);
        }else{
        }
        */
        AdapterNoIcon adp = new AdapterNoIcon(Mesas.this,ms);
        recyclerView.setAdapter(adp);
        atualizarMesas = new AtualizarMesas(this,ms,recyclerView,adp);
        atualizarMesas.start();
        RecyclerView.LayoutManager mg = new LinearLayoutManager(Mesas.this);
        recyclerView.setLayoutManager(mg);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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

        AtualizarMesas.reference=atualizarMesas;
        //fim recycler view
    }

    private boolean validateCadastroProd() {
        return true;
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        if(clickG!=0){
            clickG--;
            searchView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            searchRecyclerView.setVisibility(View.GONE);
            //recyclerViewSearch.setVisibility(View.GONE);
            t.setDisplayShowTitleEnabled(true);
            return;
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search,menu);
        return true;
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
