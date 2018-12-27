package br.com.viphost.kardenapp.VIEW.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;
import br.com.viphost.kardenapp.R;

import br.com.viphost.kardenapp.VIEW.Holder.ViewH;
import br.com.viphost.kardenapp.VIEW.PageCategoria;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterWithIcon extends RecyclerView.Adapter<ViewH> {
    private Activity context;
    private ArrayList<String> catTitle;
    private ArrayList<Integer> img;

    public AdapterWithIcon(Activity context, ArrayList<String> title) {
        this.context = context;
        this.catTitle = title;

    }

    @NonNull
    @Override
    public ViewH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_categoria,null);

        return new ViewH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewH holder, int position) {
            holder.txtTitleCategoria.setText(catTitle.get(position));
            holder.txtTitleCategoria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent t = new Intent(context, PageCategoria.class);
                    t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle params = new Bundle();
                    t.putExtra("Nome",holder.txtTitleCategoria.getText().toString());
                    context.startActivity(t);
                }
            });
    }

    @Override
    public int getItemCount() {
        int ver;
        if(catTitle == null){
            ver=0;
        }else{
            ver = catTitle.size();
        }
        return ver;
    }
}
