package br.com.viphost.kardenapp.VIEW;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import br.com.viphost.kardenapp.CONTROLLER.mutations.AdicionarPedido;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.CONTROLLER.utils.BinaryTool;
import br.com.viphost.kardenapp.CONTROLLER.utils.Memoria;
import br.com.viphost.kardenapp.MODEL.DadosPessoais;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterSingleCategoria;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

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
    private BottomSheetDialog bottomSheetDialog;
    private TextView NomeSliding;
    private TextView EmailSliding;
    private DbOpenhelper DB;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prods = Memoria.getCarrinhoMesaAtual().getProdutos();
        setContentView(R.layout.activity_carrinho);
        toolbar = findViewById(R.id.toolbar);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        floatingActionButton = findViewById(R.id.floatingG);
        iconSearch = findViewById(R.id.iconSearch);
        carShop = findViewById(R.id.imgCarrinho);
        menu = findViewById(R.id.menuUp);
        recyclerView =findViewById(R.id.recyclerCarrinho);
        DB = new DbOpenhelper(this);
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
        setSupportActionBar(toolbar);
        ActionBar t = getSupportActionBar();
        t.setTitle("Carrinho (Mesa: "+Memoria.getMesaAtual()+")");
        t.setDisplayHomeAsUpEnabled(true);
        final AdapterSingleCategoria adp = new AdapterSingleCategoria(Carrinho.this,prods);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Carrinho.this);
        bottomSheetDialog = new BottomSheetDialog(Carrinho.this);
        View modal = getLayoutInflater().inflate(R.layout.bottom_behavior,null);
        bottomSheetDialog.setContentView(modal);
        NomeSliding = modal.findViewById(R.id.NomeSliding);
        EmailSliding = modal.findViewById(R.id.EmailSliding);
        DadosPessoais dadosPessoais = DB.getDadosPessoais();
        NomeSliding.setText(dadosPessoais.getNome());
        EmailSliding.setText(dadosPessoais.getEmail());
        menu.setOnClickListener(new View.OnClickListener() {
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
                androidx.appcompat.app.AlertDialog.Builder b = new androidx.appcompat.app.AlertDialog.Builder(Carrinho.this);
                View cadastroDialog = LayoutInflater.from(Carrinho.this).inflate(R.layout.cadastro_produto,null);
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
                ArrayAdapter sp = new ArrayAdapter(Carrinho.this,R.layout.support_simple_spinner_dropdown_item,categorias);
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
                        Toast.makeText(Carrinho.this,"Escolha uma opção válida",Toast.LENGTH_SHORT).show();
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
                                new Balao(Carrinho.this, "Insira um valor no formato correto Ex.: 000.00", Toast.LENGTH_SHORT);
                                return;
                            }
                            //conexao server aqui--------------------//
                            new EnviarCadastroProduto(Carrinho.this,nomeProdutoCadastro,precoProdutoCadastro,nomeCategoria).run(true);
                            //---------------------------------------//
                            //conexao offline aqui-------------------//
                            //? vou inserir no SQLite na classe acima//
                            //---------------------------------------//
                            edtPrecoProdCad.setText("");
                            edtNomeProdCad.setText("");
                            edtNomeProdCad.findFocus();
                            alertCadastroProduto.dismiss();
                        }else{
                            Toast.makeText(Carrinho.this,"Deve digitar algum valor!",Toast.LENGTH_SHORT).show();

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
                        GraphqlResponse resposta = adicionarPedido.run(Memoria.getAtualComanda(Memoria.getMesaAtual()),itens,DB.getToken(),deviceID);
                        if(resposta instanceof br.com.viphost.kardenapp.CONTROLLER.tipos.Logico){
                            br.com.viphost.kardenapp.CONTROLLER.tipos.Logico response = (br.com.viphost.kardenapp.CONTROLLER.tipos.Logico)resposta;
                            if(response.getValor()){
                                new Balao(Carrinho.this,"Pedido Realizado",Toast.LENGTH_SHORT).show();
                                Intent m = new Intent(Carrinho.this,Pedidos.class);
                                m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(m);
                                Carrinho.this.finish();
                                Memoria.getCarrinho(Memoria.getMesaAtual()).getProdutos().clear();
                                Memoria.apagarCarrinhoMesa(Memoria.getMesaAtual());
                                Memoria.setMesaAtual("-1");
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

    private boolean validateCadastroProd() {
        return  true;
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
