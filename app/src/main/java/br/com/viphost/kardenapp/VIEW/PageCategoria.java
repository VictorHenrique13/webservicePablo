package br.com.viphost.kardenapp.VIEW;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import br.com.viphost.kardenapp.CONTROLLER.connections.pagecategoria.AtualizarProdutos;
import br.com.viphost.kardenapp.CONTROLLER.tipos.ListaProdutos;
import br.com.viphost.kardenapp.CONTROLLER.queries.ListarProdutos;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.CONTROLLER.utils.BinaryTool;
import br.com.viphost.kardenapp.CONTROLLER.utils.Memoria;
import br.com.viphost.kardenapp.MODEL.DadosPessoais;
import br.com.viphost.kardenapp.MODEL.Produto;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Adapter.AdapterSingleCategoria;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
    private TextView NomeSliding;
    private TextView EmailSliding;

//--------------------------------------------------
    private TextInputLayout layNomeProdCad;
    private TextInputLayout layPrecoCad;
    private TextInputLayout layQuantidadeCad;
    private TextInputEditText edtNomeProdCad;
    private TextInputEditText edtQuantidadeProdCad;
    private TextInputEditText edtPrecoProdCad;
    private TextView btnConfirmCad;
    private TextView btnCancelCad;
    private LinearLayout btnCadastrarProduto;
    private Spinner spinner;
    //private String[] categorias = {"Categorias"};//Preciso recriar dentro do ClickListener pois nao e possivel fixar um tamanho antes
    private String nomeCategoria;
    private androidx.appcompat.app.AlertDialog alertCadastroProduto;

    private DbOpenhelper DB;
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
        DB = new DbOpenhelper(this);
        new AtualizarPermissao(this).run(true);
        bottomSheetDialog = new BottomSheetDialog(PageCategoria.this);
        View modal = getLayoutInflater().inflate(R.layout.bottom_behavior,null);
        bottomSheetDialog.setContentView(modal);
        NomeSliding = modal.findViewById(R.id.NomeSliding);
        EmailSliding = modal.findViewById(R.id.EmailSliding);
        DadosPessoais dadosPessoais = DB.getDadosPessoais();
        NomeSliding.setText(dadosPessoais.getNome());
        EmailSliding.setText(dadosPessoais.getEmail());
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

        //funçoes menu deslizante-------------------------------
        btnCadastrarProduto = modal.findViewById(R.id.cadastrarProdutoAction);
        if(BinaryTool.BitValueOfInt(DB.getPermissao(),7)==false){
            btnCadastrarProduto.setVisibility(View.GONE);
        }
        btnCadastrarProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder b = new androidx.appcompat.app.AlertDialog.Builder(PageCategoria.this);
                View cadastroDialog = LayoutInflater.from(PageCategoria.this).inflate(R.layout.cadastro_produto,null);
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
                ArrayAdapter sp = new ArrayAdapter(PageCategoria.this,R.layout.support_simple_spinner_dropdown_item,categorias);
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
                        Toast.makeText(PageCategoria.this,"Escolha uma opção válida",Toast.LENGTH_SHORT).show();
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
                                new Balao(PageCategoria.this, "Insira um valor no formato correto Ex.: 000.00", Toast.LENGTH_SHORT);
                                return;
                            }
                            //conexao server aqui--------------------//
                            new EnviarCadastroProduto(PageCategoria.this,nomeProdutoCadastro,precoProdutoCadastro,nomeCategoria).run(true);
                            //---------------------------------------//
                            //conexao offline aqui-------------------//
                            //? vou inserir no SQLite na classe acima//
                            //---------------------------------------//
                            edtPrecoProdCad.setText("");
                            edtNomeProdCad.setText("");
                            edtNomeProdCad.findFocus();
                            alertCadastroProduto.dismiss();
                        }else{
                            Toast.makeText(PageCategoria.this,"Deve digitar algum valor!",Toast.LENGTH_SHORT).show();

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
        final AdapterSingleCategoria adp = new AdapterSingleCategoria(PageCategoria.this,prod);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PageCategoria.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adp);

        //Puxar Todas os Produtos//////////////////////////////////////////////////////////////////////////////////////
        //final int idCategoria = Memoria.getIdFromCategoria(info);
        new AtualizarProdutos(this,info,prod,recyclerView,adp).run();
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
