package br.com.criptoreal.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import br.com.criptoreal.R;
import br.com.criptoreal.config.ConfiguracaoFirebase;

/**
 * Classe que representa o primeiro contato do usuário com o app.
 * Verifica seu status no aplicativo e direciona o usuário para outros locais do aplicativo como a Tela Principal ou a Tela de Login.
 * @author Arthur Sales
 */

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verificarUsuarioLogado();
    }
    @Override
    protected void onResume() {
     cont = 0; //O zero aqui serve para que caso o usuário voltei para a tela de splash ele possa sair
       verificarUsuarioLogado();
        super.onResume();
    }


    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        if(autenticacao.getCurrentUser() != null){
            abrirTelaPrincipal();
        }else{
            IniciaSplash();
        }
    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void IniciaSplash () {

        setContentView(R.layout.activity_splash);

        new Thread(new Runnable() {
            @Override
            public void run() {
                cont ++;

                try{
                    while (cont == 1 || cont <=5){
                        Thread.sleep(1000);
                        cont++;
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                if (cont == 6){
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    //intent.putExtra("Contator",cont);
                    startActivity(intent);
                    cont++;

                }
            }
        }).start();

    }

}
