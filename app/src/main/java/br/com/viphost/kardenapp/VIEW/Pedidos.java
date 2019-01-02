package br.com.viphost.kardenapp.VIEW;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.DeviceInfo;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlClient;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlError;
import br.com.viphost.kardenapp.CONTROLLER.GraphqlResponse;
import br.com.viphost.kardenapp.CONTROLLER.connections.AtualizarPermissao;
import br.com.viphost.kardenapp.CONTROLLER.connections.menudeslizante.EnviarCadastroProduto;
import br.com.viphost.kardenapp.CONTROLLER.connections.mesas.AtualizarMesas;
import br.com.viphost.kardenapp.CONTROLLER.mutations.Logout;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.CONTROLLER.utils.BinaryTool;
import br.com.viphost.kardenapp.MODEL.DadosPessoais;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterPedidos;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterWithIcon;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class Pedidos extends AppCompatActivity {
    private AlertDialog close;

    private Toolbar toolbar;
    private BottomAppBar bottomAppBar;
    private RecyclerView recyclerView;
    private ArrayList<String> pedidos = new ArrayList<String>();
    //bottom itens
    private FloatingActionButton floatingActionButton;
    private ImageView iconSearch;
    private  ImageView menuUp;
    int clickG =0;
    //menu referencia
    private BottomSheetDialog bottomSheetDialog;
    private TextInputLayout layNomeProdCad;
    private TextInputLayout layPrecoCad;
    private TextInputLayout layQuantidadeCad;
    private TextInputEditText edtNomeProdCad;
    private TextInputEditText edtQuantidadeProdCad;
    private TextInputEditText edtPrecoProdCad;
    private TextView btnConfirmCad;
    private TextView btnCancelCad;
    private SearchView searchView;
    private LinearLayout btnCadastrarProduto;
    private LinearLayout btnIrParaPedidos;
    private Spinner spinner;
    private String[] categorias;
    //private String[] categorias = {"Categorias"};//Preciso recriar dentro do ClickListener pois nao e possivel fixar um tamanho antes
    private String nomeCategoria;
    private androidx.appcompat.app.AlertDialog alertCadastroProduto;
    private DbOpenhelper DB;
    private TextView NomeSliding;
    private  TextView EmailSliding;
    private AtualizarMesas atualizarMesas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DB = new DbOpenhelper(this);
        setContentView(R.layout.activity_pedidos);
        floatingActionButton = findViewById(R.id.floatingG);
        toolbar = findViewById(R.id.toolbar);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(toolbar);
        iconSearch = findViewById(R.id.iconSearch);
        searchView = findViewById(R.id.searchT);
        menuUp = findViewById(R.id.menuUp);
        ActionBar t = getSupportActionBar();
        t.setTitle("Pedidos");
        bottomSheetDialog = new BottomSheetDialog(Pedidos.this);
        View modal = getLayoutInflater().inflate(R.layout.bottom_behavior,null);
        bottomSheetDialog.setContentView(modal);
        NomeSliding = modal.findViewById(R.id.NomeSliding);
        EmailSliding = modal.findViewById(R.id.EmailSliding);
        DadosPessoais dadosPessoais = DB.getDadosPessoais();
        NomeSliding.setText(dadosPessoais.getNome());
        EmailSliding.setText(dadosPessoais.getEmail());
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
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Pedidos.this,Mesas.class));
            }
        });
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

        btnIrParaPedidos.setVisibility(View.GONE);
        btnIrParaPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent m = new Intent(getApplicationContext(),Pedidos.class);
                startActivity(m);
                Pedidos.this.finish();
            }
        });
        btnCadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder b = new androidx.appcompat.app.AlertDialog.Builder(Pedidos.this);
                View cadastroDialog = LayoutInflater.from(Pedidos.this).inflate(R.layout.cadastro_produto,null);
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
                ArrayAdapter sp = new ArrayAdapter(Pedidos.this,R.layout.support_simple_spinner_dropdown_item,categorias);
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
    ///troca por um arraylist que é sucesso
                        nomeCategoria = (String) parent.getItemAtPosition(position);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(Pedidos.this,"Escolha uma opção válida",Toast.LENGTH_SHORT).show();
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
                                new Balao(Pedidos.this, "Insira um valor no formato correto Ex.: 000.00", Toast.LENGTH_SHORT);
                                return;
                            }
                            //conexao server aqui--------------------//
                            new EnviarCadastroProduto(Pedidos.this,nomeProdutoCadastro,precoProdutoCadastro,nomeCategoria).run(true);
                            //---------------------------------------//
                            //conexao offline aqui-------------------//
                            //? vou inserir no SQLite na classe acima//
                            //---------------------------------------//
                            edtPrecoProdCad.setText("");
                            edtNomeProdCad.setText("");
                            edtNomeProdCad.findFocus();
                            alertCadastroProduto.dismiss();
                        }else{
                            Toast.makeText(Pedidos.this,"Deve digitar algum valor!",Toast.LENGTH_SHORT).show();

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

        //preencher dados de pedidos com as mesas que tem pedidos abertos na ordem que pediram
         recyclerView = findViewById(R.id.recyclerPedidos);
        RecyclerView.LayoutManager ln= new LinearLayoutManager(Pedidos.this);
        recyclerView.setLayoutManager(ln);
        final AdapterPedidos adp = new AdapterPedidos(Pedidos.this,pedidos);
        recyclerView.setAdapter(adp);
        atualizarMesas = new AtualizarMesas(this,pedidos,recyclerView,adp);
        atualizarMesas.start(true);
        if(AtualizarMesas.reference!=null){
            AtualizarMesas.reference.finish();
            AtualizarMesas.reference=atualizarMesas;
        }
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

    }

    @Override
    public void onBackPressed() {
        if(DB.getPermissao()>=2&&!(BinaryTool.BitValueOfInt(DB.getPermissao(),4)==false&&BinaryTool.BitValueOfInt(DB.getPermissao(),2)==true)){
            super.onBackPressed();
            return;
        }
        AlertDialog.Builder b = new AlertDialog.Builder(Pedidos.this);
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

    private boolean validateCadastroProd() {
        return true;
    }
}
