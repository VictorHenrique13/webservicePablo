package br.com.viphost.kardenapp.VIEW.Pager;

import android.content.Context;

import br.com.viphost.kardenapp.VIEW.Cadastro;
import br.com.viphost.kardenapp.VIEW.Login;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdp extends FragmentPagerAdapter {
    Context context;

    public ViewPagerAdp(Context context,FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) return new Login();
        else if(position == 1) return  new Cadastro();
        return new Login();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Login";
            case 1: return "Nova Conta";
            default: return  null;
        }
    }

}