package br.com.criptoreal.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;

import br.com.criptoreal.fragment.InicioFragment;
import br.com.criptoreal.fragment.ConversorFragment;
import br.com.criptoreal.fragment.EstudoFragment;

public class TabAdapter extends FragmentStatePagerAdapter{ // Com essa classe em extends temos um melhor aproveitamento de memoria

    private String[] tituloAbas = {"INICIO","CONVERSOR","ESTUDOS"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) { // Retorna ao Pager os fragments

        Fragment fragment = null;

        switch (position){ // Retorna qual será carregado
            case 0: fragment =  new InicioFragment();
                break;
            case 1: fragment = new ConversorFragment();
            break;
            case 2: fragment = new EstudoFragment();
            break;
        }

        return fragment;
    }

    @Override
    public int getCount() {//Retorna quantidade de abas
        return tituloAbas.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {//Recupera os títulos de cada uma das abas
        return tituloAbas[position];
    }
}
