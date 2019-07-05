package br.com.criptoreal.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.criptoreal.R;
import br.com.criptoreal.helper.Preferencias;
import de.hdodenhof.circleimageview.CircleImageView;

public class InviteActivity extends AppCompatActivity {


    private EditText emailConvidado;
    private TextView textoConvida;



    @SuppressLint("WrongViewCast")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        exibindoFotoNomeUsuario();

        emailConvidado = findViewById(R.id.email_convidado);
        textoConvida = findViewById(R.id.texto_email);

        Button botao_enviar = findViewById(R.id.btn_Enviar);
        botao_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enviarEmail();
            }
        });
    }

    private void enviarEmail(){


        String recipientList = emailConvidado.getText().toString();
        String[] recipients = recipientList.split(",");

        String message = textoConvida.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.inviteMessage);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, getString(R.string.inviteCreateChooser)));

    }
    private void exibindoFotoNomeUsuario(){
        Preferencias preferencias = new Preferencias(InviteActivity.this);
        CircleImageView imgFotoUsuario = (CircleImageView) findViewById(R.id.imgFoto);
        TextView textNomeUsuario = (TextView) findViewById(R.id.txtNome);

        //Recuperar foto e nome do UsuarioLogado
        String NomeUsuario = preferencias.getNome();
        textNomeUsuario.setText(NomeUsuario);
        String FotoUsuario = preferencias.getFotoPerfil();
        Picasso.with(InviteActivity.this).load(FotoUsuario).into(imgFotoUsuario);
    }
}
