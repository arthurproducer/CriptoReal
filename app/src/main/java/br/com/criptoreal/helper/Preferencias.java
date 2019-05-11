package br.com.criptoreal.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "criptoreal.preferencias";
    private final int MODE = 0;
    private SharedPreferences.Editor editor; //Interface para fazer edições

    //DADOS USUARIO
    private final String CHAVE_IDENTIFICADOR = "identificadorUsuarioLogado";
    private final String CHAVE_NOME = "nomeUsuarioLogado";
    private final String CHAVE_ENDERECOFOTOPERFIL = "fotoUsuarioLogado";
    private final String CHAVE_STORAGE_ID = "storageIdUsuarioLogado";
    private final String CHAVE_SEXO = "sexoUsuarioLogado";
    //private final String CHAVE_CPF = "cpfUsuarioLogado";
    /*
    private final String CHAVE_TELEFONE = "telefone";
    private final String CHAVE_TOKEN = "token";
    */



    public Preferencias(Context contextoParametro){

        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO,MODE);
        editor = preferences.edit();
    }

    public void salvarDados(String identificadorUsuario, String nomeUsuario,String storageIdUsuario, String fotoUsuario, String sexoUsuario){ //Serve para salvar dados do usuário que está logado

        editor.putString(CHAVE_IDENTIFICADOR,identificadorUsuario);
        editor.putString(CHAVE_NOME,nomeUsuario);
        editor.putString(CHAVE_ENDERECOFOTOPERFIL,fotoUsuario);
        editor.putString(CHAVE_SEXO,sexoUsuario);
        editor.putString(CHAVE_STORAGE_ID,storageIdUsuario);
        /*
        editor.putString(CHAVE_TELEFONE,telefone) ;
        editor.putString(CHAVE_TOKEN,token);
        */
        editor.commit();//Salva os dados acima
    }

    public String getIdentificador(){
        return preferences.getString(CHAVE_IDENTIFICADOR,null);
    }
    public String getNome(){
        return preferences.getString(CHAVE_NOME,null);
    }

    public String getFotoPerfil(){return preferences.getString(CHAVE_ENDERECOFOTOPERFIL, null);}

    public String getSexo(){return preferences.getString(CHAVE_SEXO, null);}

    public String getStorage(){return  preferences.getString(CHAVE_STORAGE_ID,null);}

    //public String getStatusAulas(){return  preferences.getString(CHAVE_AULAS,null);}


    /*
    public HashMap<String,String> getDadosUsuario(){

        HashMap<String,String> dadosUsuario = new HashMap<>();

        dadosUsuario.put(CHAVE_NOME,preferences.getString(CHAVE_NOME,null));
        dadosUsuario.put(CHAVE_TELEFONE,preferences.getString(CHAVE_TELEFONE,null));
        dadosUsuario.put(CHAVE_TOKEN,preferences.getString(CHAVE_TOKEN,null));

        return dadosUsuario;
    }
  */

}
