package br.com.criptoreal.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import br.com.criptoreal.Model.Usuario;
import br.com.criptoreal.R;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.helper.Base64Custom;
import br.com.criptoreal.helper.Preferencias;

public class LoginActivity extends AppCompatActivity {

    //Objetos da View
    private EditText email;
    private EditText senha;
    private Button botaoLogar;

    //Objetos necessários para salvar os dados
    private Usuario usuario;
    private String identificadorUsuarioLogado;
    private ValueEventListener valueEventListenerUsuario;

    //Objetos do Firebase
    private FirebaseAuth autenticacao;
    private DatabaseReference databasefirebaseRef;


    //Para o Facebook
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuario = new Usuario();

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        callbackManager = CallbackManager.Factory.create();

        email = (EditText) findViewById(R.id.edit_email);
        senha = (EditText) findViewById(R.id.edit_Senha);
        botaoLogar = (Button) findViewById(R.id.btn_Logar);
        LoginButton loginButton =(LoginButton) findViewById(R.id.login_button);


        botaoLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().length() == 0 || senha.getText().length() == 0){
                    Toast.makeText(LoginActivity.this, R.string.loginEmpty, Toast.LENGTH_LONG).show();

                }else{
                    usuario = new Usuario();
                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha.getText().toString());
                    validarLogin();
                }

            }
        });

        loginButton.setReadPermissions(Arrays.asList( "public_profile","email"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            Log.d("response", response.toString());

                            getData(object);

                        }
                    });

                    validarLoginFacebookToken(loginResult.getAccessToken());

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        printKeyHash();


    }

    public void validarLogin(){
        Toast.makeText(LoginActivity.this,R.string.status,Toast.LENGTH_LONG).show();
        //Colocar barra de loading

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());

                    databasefirebaseRef = ConfiguracaoFirebase.getFireBase()
                            .child("usuarios")
                             .child(identificadorUsuarioLogado);

                    valueEventListenerUsuario = new ValueEventListener() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Usuario usuarioRecuperado = dataSnapshot.getValue(Usuario.class);

                            Preferencias preferencias = new Preferencias(LoginActivity.this);//LoginActivity
                            preferencias.salvarDados(identificadorUsuarioLogado,usuarioRecuperado.getNome(),usuarioRecuperado.getStorage_id(),//pode ser autenticacao.getUid()
                                    usuarioRecuperado.getEnderecofoto(),usuarioRecuperado.getSexo());
                            abrirTelaPrincipal();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    databasefirebaseRef.addListenerForSingleValueEvent(valueEventListenerUsuario);


                    Toast.makeText(LoginActivity.this, R.string.loginSucess, Toast.LENGTH_LONG).show();

                }else{
                    try{
                        throw task.getException();

                    }catch (FirebaseAuthInvalidUserException e) {
                        Toast.makeText(LoginActivity.this, R.string.error_wrongEmail, Toast.LENGTH_LONG).show();
                        senha.setText("");//apaga senha digitada
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(LoginActivity.this, R.string.error_login_wrongPassword, Toast.LENGTH_LONG).show();
                        senha.setText("");//apaga senha digitada
                   }catch (Exception e) {
                        Toast.makeText(LoginActivity.this, R.string.error_login_otherError, Toast.LENGTH_LONG).show();
                        senha.setText("");//apaga senha digitada
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void abrirCadastroUsuario(View view){
        Intent intent = new Intent(LoginActivity.this, UserRegisterActivity.class);
        startActivity(intent);

    }

    public void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void validarLoginFacebookToken(AccessToken token) { //Somente com o Firebase (não afeta o login do usuário), USADO SOMENTE PARA AUTENTICAR NO FIREBASE, VERIFICAR OUTRA FORMA!
        Log.d("FACELOG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        autenticacao.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FACELOG", "signInWithCredential:success");//verifica se esta f

                             usuario.salvar();

                            Preferencias preferencias = new Preferencias(LoginActivity.this);//LoginActivity
                            preferencias.salvarDados(usuario.getId(),usuario.getNome(),autenticacao.getUid(),
                                    usuario.getEnderecofoto(),usuario.getSexo());
                            abrirTelaPrincipal();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FACELOG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.error_loginFacebook,
                                    Toast.LENGTH_SHORT).show();

                        }

                    }

                });



    }

    public void getData(JSONObject object) {
        try{
            URL profile_picture = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");

            usuario.setEnderecofoto(profile_picture.toString());
            usuario.setEmail(object.getString("email"));

            Profile profile = Profile.getCurrentProfile();
            if(profile != null){

                usuario.setNome(profile.getName());

            }
            identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());
            usuario.setId(identificadorUsuarioLogado);//MUDAR PARA O ACCESS TOKEN FUTURAMENTE

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void printKeyHash(){
        //Hash necessário, para developer Facebook
        try{

            PackageInfo info = getPackageManager() .getPackageInfo("br.com.criptoreal", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d( "KeyHash", Base64.encodeToString(md.digest() , Base64.DEFAULT));
            }

        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }
}
