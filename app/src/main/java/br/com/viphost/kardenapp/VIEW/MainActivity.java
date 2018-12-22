package br.com.viphost.kardenapp.VIEW;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.utils.Database;
import br.com.viphost.kardenapp.R;
import br.com.viphost.kardenapp.VIEW.Pager.ViewPagerAdp;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static br.com.viphost.kardenapp.R.layout.fragment_login;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextInputLayout layEmail;
    private TextInputLayout laySenha;
    private TextInputEditText senha;
    private TextInputEditText email;
    private AlertDialog close;
    private Button btnGo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = new DbOpenhelper(this).getToken();
        //if(!token.isEmpty()&&token!="vazio"){//Auto Ir Mesas Activity se ja logado
           // Intent m = new Intent(getApplicationContext(),Mesas.class);
           // startActivity(m);
            //finish();
           // return;
       // }
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
