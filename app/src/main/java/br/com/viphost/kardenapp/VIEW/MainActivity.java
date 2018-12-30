package br.com.viphost.kardenapp.VIEW;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.connections.AtualizarPermissao;
import br.com.viphost.kardenapp.CONTROLLER.utils.BinaryTool;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Pager.ViewPagerAdp;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextInputLayout layEmail;
    private TextInputLayout laySenha;
    private TextInputEditText senha;
    private TextInputEditText email;
    private AlertDialog close;
    private Button btnGo;
    private DbOpenhelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DB = new DbOpenhelper(this);
        DB.checkTables();
        String token = DB.getToken();
        if(!token.isEmpty()&&token!="vazio"){//Auto Ir Activity se ja logado
            new AtualizarPermissao(this).run(true);
            int permissao = DB.getPermissao();
            if(permissao <=1){
                //Enviar para uma activityVazia para ficar esperando uma mudança de permissao
                Intent m = new Intent(getApplicationContext(),Pedidos.class);
                //Intent m = new Intent(getApplicationContext(),vazio.class);
                startActivity(m);
            }else if(permissao == 2){
                Intent m = new Intent(getApplicationContext(),Pedidos.class);
                //Intent m = new Intent(getApplicationContext(),Cozinha.class);
                startActivity(m);
            }else if(BinaryTool.BitValueOfInt(permissao,4)==false&&BinaryTool.BitValueOfInt(permissao,2)==true){
                Intent m = new Intent(getApplicationContext(),Pedidos.class);
                //Intent m = new Intent(getApplicationContext(),Cozinha.class);
                startActivity(m);
            }else{
                Intent m = new Intent(getApplicationContext(),Mesas.class);
                startActivity(m);
            }
            finish();
            return;
        }else{
            DB.recreateAllTables();
            DB.insertCategoria("Precisa Atualizar Primeiro");
        }
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.containerTabs);
        viewPager = findViewById(R.id.viewPager);
        ViewPagerAdp v = new ViewPagerAdp(MainActivity.this,getSupportFragmentManager());
        viewPager.setAdapter(v);
        tabLayout.setupWithViewPager(viewPager);



    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        b.setMessage("Deseja realmente sair do aplicativo?");
        b.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    }


}
