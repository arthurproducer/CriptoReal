package br.com.criptoreal.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Permissao {


    public static boolean validaPermissoes(int requestCode, Activity activity, String[] permissoes){

        if(Build.VERSION.SDK_INT >= 23){

            List<String> listaPermissoes = new ArrayList<String>();

            /*Percorrer as permissões passadas, verificando uma a uma
            se já tem a permissão liberada e se ela é igual ao nivel do pacote instalado no APP */
            for(String permissao : permissoes){

                Boolean validaPermissao = ContextCompat.checkSelfPermission(activity,permissao) == PackageManager.PERMISSION_GRANTED;

                if(!validaPermissao) listaPermissoes.add(permissao);
                Log.i("Tratando Foto","verificando permissão " + validaPermissao);
                Log.i("Tratando Foto","Tamanho da lista de permissões: " + listaPermissoes.size());
            }

            //Caso a lista esteja vazia, não é necessário solicitar permissão
            if(listaPermissoes.isEmpty()){return true;}

            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes); // Conversão necessária pois é esperado um Array de String

            //Solicita permissão
            Log.i("Tratando Foto","Primeiro request feito!");
            ActivityCompat.requestPermissions(activity,novasPermissoes,requestCode);
            return false; //Return false caso ainda não tenha sido permitido o acesso a camera

        }

        return true; //Segue com os procedimentos nas versões anteriores

    }


}
