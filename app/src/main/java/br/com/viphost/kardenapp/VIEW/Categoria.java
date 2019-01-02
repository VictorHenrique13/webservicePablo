package br.com.viphost.kardenapp.VIEW;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import br.com.viphost.kardenapp.CONTROLLER.connections.categoria.AtualizarCategorias;
import br.com.viphost.kardenapp.CONTROLLER.connections.menudeslizante.EnviarCadastroProduto;
import br.com.viphost.kardenapp.CONTROLLER.tipos.ListaCategorias;
import br.com.viphost.kardenapp.CONTROLLER.mutations.CadastrarCategoria;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarCategorias;
import br.com.viphost.kardenapp.CONTROLLER.connections.mesas.AtualizarMesas;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.CONTROLLER.utils.BinaryTool;
import br.com.viphost.kardenapp.CONTROLLER.utils.Memoria;
import br.com.viphost.kardenapp.MODEL.DadosPessoais;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterWithIcon;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
    private SearchView searchView;
    private ImageView carShop;
    private  ImageView menuUp;
    private  String texto;

    private BottomSheetDialog bottomSheetDialog;
    private LinearLayout btnCadastrarProdutoDialog;
    private TextView NomeSliding;
    private TextView EmailSliding;

    //-----------------menu referencias
    int clickG = 0;
    private TextInputLayout layNomeProdCad;
    private TextInputLayout layPrecoCad;
    private TextInputLayout layQuantidadeCad;
    private TextInputEditText edtNomeProdCad;
    private TextInputEditText edtQuantidadeProdCad;
    private TextInputEditText edtPrecoProdCad;
    private TextView btnConfirmCad;
    private TextView btnCancelCad;
    private LinearLayout btnCadastrarProduto;
    private LinearLayout btnIrParaPedidos;
    private Spinner spinner;
    //private String[] categorias = {"Categorias"};//Preciso recriar dentro do ClickListener pois nao e possivel fixar um tamanho antes
    private String nomeCategoria;
    private androidx.appcompat.app.AlertDialog alertCadastroProduto;
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
        searchView = findViewById(R.id.searchT);
        carShop =findViewById(R.id.imgCarrinho);
        menuUp = findViewById(R.id.menuUp);
        DB = new DbOpenhelper(this);

        setSupportActionBar(toolbar);
        ActionBar t = getSupportActionBar();
        t.setTitle("Categoria");
        t.setDisplayHomeAsUpEnabled(true);

        new AtualizarPermissao(this).run(true);
        if(BinaryTool.BitValueOfInt(DB.getPermissao(),6)==false){
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams)floatingActionButton.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            p.width = 0;
            p.height = 0;
            floatingActionButton.setLayoutParams(p);
            floatingActionButton.hide();
            //((View) floatingActionButton).setVisibility(View.GONE);
            //Esconder o botao de cadastro
        }
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(clickG==0){
                    t.setDisplayShowTitleEnabled(false);
                    searchView.setVisibility(View.VISIBLE);
                    searchView.setIconified(false);
                    searchView.setActivated(false);
                    clickG++;
                }else{
                    clickG--;
                    searchView.setVisibility(View.GONE);
                    t.setDisplayShowTitleEnabled(true);
                }

            }
        });
        carShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Categoria.this,Carrinho.class));
            }
        });
        bottomSheetDialog = new BottomSheetDialog(Categoria.this);
        View modal = getLayoutInflater().inflate(R.layout.bottom_behavior,null);
        bottomSheetDialog.setContentView(modal);
        NomeSliding = modal.findViewById(R.id.NomeSliding);
        EmailSliding = modal.findViewById(R.id.EmailSliding);
        DadosPessoais dadosPessoais = DB.getDadosPessoais();
        NomeSliding.setText(dadosPessoais.getNome());
        EmailSliding.setText(dadosPessoais.getEmail());

        menuUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });
        //funçoes menu deslizante-------------------------------
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
        btnCadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder b = new androidx.appcompat.app.AlertDialog.Builder(Categoria.this);
                View cadastroDialog = LayoutInflater.from(Categoria.this).inflate(R.layout.cadastro_produto,null);
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
                ArrayAdapter sp = new ArrayAdapter(Categoria.this,R.layout.support_simple_spinner_dropdown_item,categorias);
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
                        Toast.makeText(Categoria.this,"Escolha uma opção válida",Toast.LENGTH_SHORT).show();
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
                                new Balao(Categoria.this, "Insira um valor no formato correto Ex.: 000.00", Toast.LENGTH_SHORT);
                                return;
                            }
                            //conexao server aqui--------------------//
                            new EnviarCadastroProduto(Categoria.this,nomeProdutoCadastro,precoProdutoCadastro,nomeCategoria).run(true);
                            //---------------------------------------//
                            //conexao offline aqui-------------------//
                            //? vou inserir no SQLite na classe acima//
                            //---------------------------------------//
                            edtPrecoProdCad.setText("");
                            edtNomeProdCad.setText("");
                            edtNomeProdCad.findFocus();
                            alertCadastroProduto.dismiss();
                        }else{
                            Toast.makeText(Categoria.this,"Deve digitar algum valor!",Toast.LENGTH_SHORT).show();

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






        //---------------------------------------------------
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
                                    GraphqlResponse resposta = cadastrarMesa.run(edtCategoria.getText().toString(), DB.getToken(),deviceID);
                                    if(resposta instanceof br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro){
                                        br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro response = (br.com.viphost.kardenapp.CONTROLLER.tipos.Inteiro)resposta;
                                        new AtualizarCategorias(Categoria.this, f, recyclerView, adp).run();
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
        //Puxar Todas as categorias/////////////////////////////////////////////////////////////////////////////////////
        new AtualizarCategorias(this,f/*,f_ids*/,recyclerView,adp).run();
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        ///////////GAMBIARRA/////////
        Memoria.setCategorias(f,f_ids);
        ////////GAMBIARRA////////////
    }

    private boolean validateCadastroProd() {
        return true;
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
