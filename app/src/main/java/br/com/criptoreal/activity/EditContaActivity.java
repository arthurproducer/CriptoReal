package br.com.criptoreal.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import br.com.criptoreal.Model.Usuario;
import br.com.criptoreal.R;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.helper.Base64Custom;
import br.com.criptoreal.helper.Constantes;
import br.com.criptoreal.helper.Preferencias;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditContaActivity extends AppCompatActivity {

    private CircleImageView imgFotoUsuario;
    private TextView textNomeUsuario;
    private TextView email;
    private EditText senhaAtual;
    private EditText novaSenha;
    private EditText confsenha;
    private Button btnSalvar;
    private ImageView imgEditEmail;
    private RadioGroup radioGroup;
    private RadioButton sexoEscolhido;
    private String emailUsuarioLogado;
    private Preferencias preferencias;
    private FirebaseAuth autenticacao;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_conta);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        imgFotoUsuario = (CircleImageView) findViewById(R.id.imgFotoUser);
        textNomeUsuario = (TextView) findViewById(R.id.textIntro);
        email = (TextView) findViewById(R.id.editEmail);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupSexo);
        senhaAtual = (EditText) findViewById(R.id.editValidaSenhaAtual);
        novaSenha = (EditText) findViewById(R.id.editSenha);
        confsenha = (EditText) findViewById(R.id.editConfsenha);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        imgEditEmail = (ImageView) findViewById(R.id.imgEditEmail);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Conta");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left); //a função da seta foi atribuida no AndroidManifest.xml
        setSupportActionBar(toolbar); // metodo de suporte apenas para que funcione normalmente

        exibindoDadosUsuario();

        imgEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogEditEmail();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verificaSenhaAlterada();

            }
        });

    }

    public void verificaSenhaAlterada() { //Posso criar uma classe ou método único para tratar tanto da alteração de e-mail quanto de senha
        preferencias = new Preferencias(EditContaActivity.this);
        emailUsuarioLogado = Base64Custom.decodificarBase64(preferencias.getIdentificador());


        if(novaSenha.getText().length() >= 1 && confsenha.getText().length() >= 1){//Se uma nova senha foi inserida

            //Verifica se foi digitado algo na senhaAtual e o e-mail é o do usuario logado
            if (senhaAtual.getText().length() >= 1 && emailUsuarioLogado.equals(email.getText().toString())) {//Se a senha atual foi digitada
                alterarSenha();
            }
        }else{
            verificaSexoAlterado();
        }
    }

    public void abrirDialogEditEmail(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(EditContaActivity.this);
        final AlertDialog alertDialog = builder.create();

        //Configurações da Dialog
        //alertDialog.setMessage("Para alterar o seu e-mail será necessário informar sua senha atual.");
        alertDialog.setCancelable(false);

        final LayoutInflater inflater = EditContaActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_edit_email, null);
        alertDialog.setView(view);//Pode ser feito com botões, caixa de texto, enfim qualquer coisa que será exibida

         final EditText novoEmail = (EditText) view.findViewById(R.id.editNovoEmail);
        final EditText senhaAtual = (EditText) view.findViewById(R.id.editValidaSenhaAtual);
        Button btnCancelar = (Button) view.findViewById(R.id.btnCancelar);
        Button btnAlterar = (Button) view.findViewById(R.id.btnAlterar);

        btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String novoemail = novoEmail.getText().toString();

                String senhaatual = senhaAtual.getText().toString();

                preferencias = new Preferencias(EditContaActivity.this);//Repetido em outro método cogitar trocar
                emailUsuarioLogado = Base64Custom.decodificarBase64(preferencias.getIdentificador());//Repetido em outro método

                //Verifica se foi digitado algo na senhaAtual e o e-mail é o do usuario logado
                if (senhaatual.length() >= 1 && novoemail.length() >=1) {//Se a senha atual foi digitada e o novo email
                    alterarEmail(emailUsuarioLogado,senhaAtual.getText().toString(),novoEmail.getText().toString(),preferencias.getIdentificador());
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();/// sai de tudo
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public void alterarEmail(String email, String senha, final String novoEmail, final String idAntigoEmail){
        Toast.makeText(EditContaActivity.this, "Aguarde estamos verificando...", Toast.LENGTH_LONG).show();//Colocar barra de loading
        ///DEVO COLOCAR O TRATAMENTO DE ERRO APLICADO NO CADASTRO
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(
               email, senha
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                        //Alterar email
                        autenticacao.getCurrentUser().updateEmail(novoEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditContaActivity.this, "Email Alterado!!", Toast.LENGTH_LONG).show();

                                            String novoIdUsuarioLogado = Base64Custom.codificarBase64(novoEmail);//Repetido em outro método

                                            Usuario usuario = new Usuario();
                                            usuario.setId(novoIdUsuarioLogado);
                                            usuario.setNome(preferencias.getNome());
                                            usuario.setSexo(preferencias.getSexo());
                                            usuario.setStorage_id(preferencias.getStorage());
                                            usuario.setEnderecofoto(preferencias.getFotoPerfil());
                                            usuario.setEmail(novoEmail);

                                            DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFireBase();
                                            //MANDAR TODAS AS AULAS PARA ESSE NOVO ID
                                            DatabaseReference idAntigo =  referenciaFirebase.child("aulas").child(idAntigoEmail);
                                            DatabaseReference idNovo = referenciaFirebase.child("aulas").child(novoIdUsuarioLogado);
                                            Log.i("PREFERENCIAS_OLD",idAntigoEmail);
                                            Log.i("PREFERENCIAS_NEW",novoIdUsuarioLogado);
                                            copiandoAulas(idAntigo,idNovo);


                                            //APAGA ANTIGO ID
                                            referenciaFirebase.child("usuarios").child(preferencias.getIdentificador()).removeValue();
                                            Log.i("PREFERENCIAS_pre", preferencias.getIdentificador());
                                            preferencias.salvarDados(novoIdUsuarioLogado,preferencias.getNome(),preferencias.getStorage(),preferencias.getFotoPerfil(),preferencias.getSexo());
                                            Log.i("PREFERENCIAS_pos", preferencias.getIdentificador());
                                            usuario.salvar();


                                        }else {
                                            try {
                                                throw task.getException();

                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                Toast.makeText(EditContaActivity.this, "Erro: O e-mail digitado é invalido, digite um novo e-mail!", Toast.LENGTH_LONG).show();
                                            } catch (FirebaseAuthUserCollisionException e) {
                                                Toast.makeText(EditContaActivity.this, "Erro: Esse e-mail já está em uso no App! Tente outro!", Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                                Toast.makeText(EditContaActivity.this, "Erro: Ao alterar e-mail!", Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                } else {
                    try {
                        throw task.getException();

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(EditContaActivity.this, "Erro: Senha Atual incorreta, tente novamente!", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(EditContaActivity.this, "Erro ao validar Senha!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void copiandoAulas(final DatabaseReference antigoPath, final DatabaseReference novoPath){
        Log.i("PREFERENCIAS_OLD_PATH",antigoPath.getKey());
        Log.i("PREFERENCIAS_NEW_PATH",novoPath.getKey());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                novoPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()){

                            ///CONTINUA VINDO COM MODULOS FALSOS

                            Toast.makeText(EditContaActivity.this, "Aulas migradas a novo e-mail!", Toast.LENGTH_LONG).show();
                            Log.i("TESTES","ESTOU DENTRO");


                        }else{
                            Toast.makeText(EditContaActivity.this, "Erro: Ao pegar as aulas do antigo e-mail!", Toast.LENGTH_LONG).show();
                        }
                        ConfiguracaoFirebase.getFireBase().child("aulas").child(antigoPath.getKey()).removeValue();
                        Log.i("TESTES", "ESTOU Excluindo AULA");
                        abrirTelaPrincipal();
                        Log.i("TESTES", "ESTOU FORA DO TASK E DENTRO DO DATA CHANGE");
                    }
                    ///LIMBO
                });
                Log.i("TESTES", "ESTOU DENTRO DO DATA CHANGE"); //AQUI NÃO PODE EXCLUIR SEU IDIOTA
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };

        antigoPath.addListenerForSingleValueEvent(valueEventListener);

    }

    public void exibindoDadosUsuario() {

        preferencias = new Preferencias(EditContaActivity.this);

        //Recuperar foto e nome do UsuarioLogado
        String nomeUsuario = preferencias.getNome();
        textNomeUsuario.setText(nomeUsuario + ". Edite suas configurações de conta e altere sua senha aqui.");

        emailUsuarioLogado = Base64Custom.decodificarBase64(preferencias.getIdentificador());
        email.setText(emailUsuarioLogado);

        try{
        String sexo = preferencias.getSexo();
        if (sexo.equals("Masculino")) {
            RadioButton radioMasc = (RadioButton) findViewById(R.id.radioBtnMasculino);
            radioMasc.toggle();
        } else {
            RadioButton radioFem = (RadioButton) findViewById(R.id.radioBtnFeminino);
            radioFem.toggle();
        }}catch(NullPointerException ex){
            RadioButton radioMasc = (RadioButton) findViewById(R.id.radioBtnMasculino);
            radioMasc.toggle();
        }

        String FotoUsuario = preferencias.getFotoPerfil();
        Picasso.with(EditContaActivity.this).load(FotoUsuario).into(imgFotoUsuario);
    }

    public void alterarSenha() {
        Toast.makeText(EditContaActivity.this, "Aguarde estamos verificando...", Toast.LENGTH_LONG).show();//Colocar barra de loading
        ///DEVO COLOCAR O TRATAMENTO DE ERRO APLICADO NO CADASTRO
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(
                email.getText().toString(),
                senhaAtual.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {


                    if (novaSenha.getText().toString().equals(confsenha.getText().toString())) {


                        //Alterar senha
                        autenticacao.getCurrentUser().updatePassword(novaSenha.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditContaActivity.this, "Senha Alterada!!", Toast.LENGTH_LONG).show();

                                            abrirTelaPrincipal();
                                        }else{
                                            try{
                                                throw task.getException();

                                            }catch (FirebaseAuthWeakPasswordException e){
                                                Toast.makeText(EditContaActivity.this, "Erro: Digite uma senha mais forte, contendo mais caracteres e com letras e números!", Toast.LENGTH_LONG).show();
                                            }catch (Exception e) {
                                                Toast.makeText(EditContaActivity.this, "Erro: Ao efetuar o cadastro!", Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                    }else{
                            Toast.makeText(EditContaActivity.this, "A nova senha não condiz com sua repetição, tente novamente !", Toast.LENGTH_LONG).show();
                        novaSenha.setText("");//apaga senha digitada
                        confsenha.setText("");
                    }

                } else {
                    try {
                        throw task.getException();

                    } catch (FirebaseAuthInvalidUserException e) { //Impossível acontecer?
                        Toast.makeText(EditContaActivity.this, "Erro: O e-mail informado não existe ou está desativado", Toast.LENGTH_LONG).show();
                        senhaAtual.setText("");//apaga senha digitada
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(EditContaActivity.this, "Erro: Senha Atual incorreta, tente novamente!", Toast.LENGTH_LONG).show();
                        senhaAtual.setText("");//apaga senha digitada
                    } catch (Exception e) {
                        Toast.makeText(EditContaActivity.this, "Erro ao validar Senha!", Toast.LENGTH_LONG).show();
                        senhaAtual.setText("");//apaga senha digitada
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void verificaSexoAlterado(){
    Usuario usuario = new Usuario();

        int idsexoEscolhido = radioGroup.getCheckedRadioButtonId();
        if(idsexoEscolhido > 0){
            sexoEscolhido = (RadioButton) findViewById(idsexoEscolhido);
            usuario.setSexo(sexoEscolhido.getText().toString());
        }

        //Tratar SexoAlterado
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFireBase();
        referenciaFirebase.child("usuarios").child(preferencias.getIdentificador()).child("sexo").setValue(usuario.getSexo());
        preferencias.salvarDados(preferencias.getIdentificador(),preferencias.getNome(),preferencias.getStorage(),preferencias.getFotoPerfil(),usuario.getSexo());
        Log.i("Tratando Foto","Veja como ficou as preferencias" + preferencias.getIdentificador() + preferencias.getNome() + preferencias.getFotoPerfil() + preferencias.getSexo());
        Log.i("Tratando Foto","Veja como ficou o usuario" + usuario.getId() + usuario.getNome() + usuario.getEnderecofoto());

        Toast.makeText(EditContaActivity.this, "Sexo Alterado!!", Toast.LENGTH_LONG).show();
        abrirTelaPrincipal();
    }

    public void abrirTelaPrincipal(){

        Intent intent = new Intent(EditContaActivity.this, MainActivity.class);
        startActivity(intent);
        Log.i("TESTES","FUI PARA TELA PRINCIPAL");
    }


}