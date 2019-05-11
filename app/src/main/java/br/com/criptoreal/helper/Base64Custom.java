package br.com.criptoreal.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codificarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(),Base64.DEFAULT).replaceAll("(\\n|\\r)",""); // Criptografa, o replaceAll é para que não conte quebras de linhas ou caracteres indesejados
    }

    public static String decodificarBase64(String textocodificado){
        return new String(Base64.decode(textocodificado,Base64.DEFAULT)); //A String aqui serve para converter o valor de bytes, pois o método requer uma string
    }


}
