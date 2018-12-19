package br.com.viphost.kardenapp.CONTROLLER.utils;

import android.app.Activity;
import android.widget.Toast;

public class Balao {
    private Activity activity;
    private String text;
    private int duration;
    public Balao(Activity activity, String text, int duration){
        this.activity=activity;
        this.text=text;
        this.duration=duration;
    }

    public void show(){
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Balao.this.activity.getApplicationContext(),Balao.this.text,Balao.this.duration).show();
            }
        });
    }
}
