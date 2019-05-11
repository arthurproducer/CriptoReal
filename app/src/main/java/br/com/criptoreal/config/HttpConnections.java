package br.com.criptoreal.config;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnections {
    //método get
    public static String get(String urlString){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null; //Faz a leitura linha a linha
        String resposta = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }
            resposta = buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            try {
                reader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return resposta;
    }
}
