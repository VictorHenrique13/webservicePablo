package br.com.viphost.kardenapp.VIEW;

import androidx.appcompat.app.AppCompatActivity;
import br.com.viphost.kardenapp.R;

import android.os.Bundle;
import android.widget.Toolbar;

import com.google.android.material.bottomappbar.BottomAppBar;

public class Pedidos extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomAppBar bottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
    }
}
