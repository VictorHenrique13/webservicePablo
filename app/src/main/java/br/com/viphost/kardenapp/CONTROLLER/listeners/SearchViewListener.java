package br.com.viphost.kardenapp.CONTROLLER.listeners;

import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import br.com.viphost.kardenapp.CONTROLLER.DAO.DbOpenhelper;
import br.com.viphost.kardenapp.CONTROLLER.utils.Balao;

public class SearchViewListener implements SearchView.OnQueryTextListener {
    private final Activity activity;
    private final RecyclerView.Adapter adapter;
    private final ArrayList<String> array;
    private final DbOpenhelper DB;
    public SearchViewListener(Activity activity, RecyclerView.Adapter adapter, ArrayList<String> array){
        this.activity=activity;
        this.adapter=adapter;
        this.array=array;
        DB=new DbOpenhelper(activity);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        new Balao(activity,newText, Toast.LENGTH_SHORT);
        return true;
    }
}
