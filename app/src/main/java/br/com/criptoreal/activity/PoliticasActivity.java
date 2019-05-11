package br.com.criptoreal.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.criptoreal.R;
import br.com.criptoreal.helper.Preferencias;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.criptoreal.helper.Constantes.URL_POLITICAS_PRIVACIDADE;

public class PoliticasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_politicas);

        exibindoFotoNomeUsuario();

        WebView politicasWV = (WebView) findViewById(R.id.webView1);

        WebSettings politicasWS = politicasWV.getSettings();
        politicasWS.setJavaScriptEnabled(true);
        politicasWS.setSupportZoom(false);

        politicasWV.loadUrl(URL_POLITICAS_PRIVACIDADE);
        politicasWV.setWebChromeClient(new WebChromeClient());
        politicasWV.setWebViewClient(new WebViewClient());

    }

    private void exibindoFotoNomeUsuario(){
        Preferencias preferencias = new Preferencias(PoliticasActivity.this);
        CircleImageView imgFotoUsuario = (CircleImageView) findViewById(R.id.imgFoto);
        TextView textNomeUsuario = (TextView) findViewById(R.id.txtNome);

        //Recuperar foto e nome do UsuarioLogado
        String NomeUsuario = preferencias.getNome();
        textNomeUsuario.setText(NomeUsuario);
        String FotoUsuario = preferencias.getFotoPerfil();
        Picasso.with(PoliticasActivity.this).load(FotoUsuario).into(imgFotoUsuario);
    }
}
