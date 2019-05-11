package br.com.criptoreal.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import br.com.criptoreal.helper.Constantes;
import br.com.criptoreal.helper.Permissao;
import de.hdodenhof.circleimageview.CircleImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import br.com.criptoreal.Model.Usuario;
import br.com.criptoreal.R;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.helper.Base64Custom;
import br.com.criptoreal.helper.Preferencias;

import static br.com.criptoreal.helper.Constantes.ENDERECO_FOTO_PADRAO;
import static br.com.criptoreal.helper.Constantes.URL_POLITICAS_PRIVACIDADE;

public class CadastroUsuarioActivity extends AppCompatActivity implements View.OnClickListener{

    //Constante para rastrear a intent do seletor de imagens
    private static final int PICK_IMAGE_REQUEST = 234;//REQUEST necessário para pegar a foto, porém seu valor pode ser qualquer número.
    //private static final int FOTO_TIRADA = 32;//REQUEST necessário para pegar a foto, porém seu valor pode ser qualquer número.

    //Objetos da View
    private EditText nome;
    private EditText email;
    private EditText senha;
    private EditText confsenha;
    private Button botaoCadastrar;

    private TextView carregarFoto;
    private TextView tirarFoto;
    //private EditText cpf;
    private RadioGroup radioGroup;
    private RadioButton sexoEscolhido;
    private CircleImageView foto;
    private CheckBox checkTermos;

    //Utilizado para armazenar os dados a serem salvos
    private Usuario usuario;
    private String identificadorUsuario;

    //Uri para arquivar arquivo
    private Uri filePatch;

    //Objetos Firebase
    private FirebaseAuth autenticacao;
    private StorageReference storageFirebaseRef;
    private Preferencias preferencias;

    //SOLICITAÇÃO DE PERMISSÃO
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        preferencias = new Preferencias(CadastroUsuarioActivity.this);
        storageFirebaseRef = ConfiguracaoFirebase.getStorageRef();

    nome = (EditText) findViewById(R.id.editcadastronome);
    email = (EditText) findViewById(R.id.editcadastroemail);
    senha = (EditText) findViewById(R.id.editcadastrosenha);
    confsenha = (EditText) findViewById(R.id.editcadastroconfsenha);
    botaoCadastrar = (Button) findViewById(R.id.btn_Cadastrar);

    carregarFoto= (TextView) findViewById(R.id.textCarregueFoto);
    tirarFoto = (TextView) findViewById(R.id.textTireFoto);
    //cpf = (EditText) findViewById(R.id.editcadastrocpf);
    radioGroup = (RadioGroup) findViewById(R.id.radioGroupSexo);
    foto = (CircleImageView) findViewById(R.id.imgNovaFoto);
    Picasso.with(CadastroUsuarioActivity.this).load(Constantes.ENDERECO_FOTO_PADRAO).into(foto);

    botaoCadastrar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            checkTermos = (CheckBox) findViewById(R.id.checkTermos);
            if(checkTermos.isChecked()){
            usuario = new Usuario();
            usuario.setNome(nome.getText().toString());
            usuario.setEmail(email.getText().toString());
            usuario.setSenha(senha.getText().toString());
            usuario.setConfsenha(confsenha.getText().toString());
            //usuario.setCpf(cpf.getText().toString());
            int idsexoEscolhido = radioGroup.getCheckedRadioButtonId();
            if(idsexoEscolhido > 0){
                sexoEscolhido = (RadioButton) findViewById(idsexoEscolhido);
                usuario.setSexo(sexoEscolhido.getText().toString());
            }

            identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
            usuario.setId(identificadorUsuario);

            verificaCampoObrigatorio();

        }else{
                Toast.makeText(getApplicationContext(), "Aceito os termos antes de prosseguir !!", Toast.LENGTH_SHORT).show();
            }
        }
    });

//Tratando a WebView das Politicas de Privacidade

        WebView politicasWV = (WebView) findViewById(R.id.webView1);

        WebSettings politicasWS = politicasWV.getSettings();
        politicasWS.setJavaScriptEnabled(true);
        politicasWS.setSupportZoom(false);

        politicasWV.loadUrl(URL_POLITICAS_PRIVACIDADE);
        politicasWV.setWebChromeClient(new WebChromeClient());
        politicasWV.setWebViewClient(new WebViewClient());

    }

    @Override
    public void onClick(View view) {
        if(view == carregarFoto){
            mostreArquivoEscolhido();
        }
        else if(view == tirarFoto){

                    if(Permissao.validaPermissoes(1,this, permissoesNecessarias)){
                        tirarFoto();//Somente entra aqui quando as permissões já foram permitidas
                    }
        }
    }

    public void verificaCampoObrigatorio(){

        if(nome.getText().length() != 0 && email.getText().length() != 0 && senha.getText().length() != 0 && confsenha.getText().length() != 0) {

            if(usuario.getSenha().equals(usuario.getConfsenha())) {
             cadastrarUsuario();
            }else{
                Toast.makeText(CadastroUsuarioActivity.this, "As senhas não condizem, tente novamente", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(CadastroUsuarioActivity.this, "Preencha todos os campos!", Toast.LENGTH_LONG).show();
            if(nome.getText().length() == 0){
                nome.setError("Campo Obrigatório");
            }
            if(email.getText().length() == 0){
                email.setError("Campo Obrigatório");
            }
            if(senha.getText().length() == 0){
                senha.setError("Campo Obrigatório");
            }
            if(confsenha.getText().length() == 0){
                confsenha.setError("Campo Obrigatório");
            }
        }
    }

    public void uploadFotoPerfil(final String uid) {
        /*
        //VERIFICAR
        //Exibindo um progressDialog enquanto a imagem é upada //MELHORAR PARA OTIMIZAÇÃO
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
*/
        StorageReference storageRef = storageFirebaseRef.child(Constantes.STORAGE_PATH_UPLOADS + uid + "/" + System.currentTimeMillis() + "." + getFileExtension(filePatch));
        storageRef.putFile(filePatch)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(getApplicationContext(), "Sucesso ao cadastrar foto do usuário!!", Toast.LENGTH_SHORT).show();

                        //pegando o endereço da foto no Storage
                        usuario.setEnderecofoto(taskSnapshot.getDownloadUrl().toString());

                        preferencias.salvarDados(identificadorUsuario, usuario.getNome(),uid,usuario.getEnderecofoto(),usuario.getSexo());

                        usuario.salvar();

                       //dismissing the progress dialog *TEMPORARIO*
                        //progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Não foi possível enviar esta foto!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Foto sendo armazenada...", Toast.LENGTH_SHORT).show();

                        /*
                        //O que ocorre durante o Upload
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        */
                    }
                });
    }

    public void cadastrarUsuario(){

            autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
            autenticacao.createUserWithEmailAndPassword(
                    usuario.getEmail(),
                    usuario.getSenha()
            ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            //Aqui é pego o ID do Storage
                            usuario.setStorage_id(autenticacao.getUid());


                            if (filePatch != null) {//O método da foto só será chamado caso o usuário tenha colocado uma foto,
                                uploadFotoPerfil(autenticacao.getUid());//VRIFICARE

                            } else {
                                usuario.setEnderecofoto(ENDERECO_FOTO_PADRAO);
                                // Foto padrão pois usuário não tiro nem carregou foto
                                preferencias.salvarDados(identificadorUsuario, usuario.getNome(), usuario.getStorage_id(), usuario.getEnderecofoto(), usuario.getSexo());
                                usuario.salvar();
                            }

                            Toast.makeText(CadastroUsuarioActivity.this, "Sucesso ao cadastrar usuario!!", Toast.LENGTH_SHORT).show();
                            finish(); //Encerra a Activity, voltando para a tela de Login


                        } else {
                            try {

                            throw task.getException();

                        }catch(FirebaseAuthWeakPasswordException e){
                            Toast.makeText(CadastroUsuarioActivity.this, "Erro: Digite uma senha mais forte, contendo mais caracteres e com letras e números!", Toast.LENGTH_LONG).show();
                        }catch(FirebaseAuthInvalidCredentialsException e){
                            Toast.makeText(CadastroUsuarioActivity.this, "Erro: O e-mail digitado é invalido, digite um novo e-mail!", Toast.LENGTH_LONG).show();
                        }catch(FirebaseAuthUserCollisionException e){
                            Toast.makeText(CadastroUsuarioActivity.this, "Erro: Esse e-mail já está em uso no App!", Toast.LENGTH_LONG).show();
                        }catch(Exception e){
                            Toast.makeText(CadastroUsuarioActivity.this, "Erro: Ao efetuar o cadastro!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }
                    }


            });
    }

    public void mostreArquivoEscolhido(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void tirarFoto(){
        //View view
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent,0);
        }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode ,data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePatch = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePatch);
                foto.setImageBitmap(bitmap);//aqui é definido qual das imagens vai receber o método mostrarArquivoEscolhido
            }catch (IOException e){
                e.printStackTrace();
            }}

            else if(data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap img = (Bitmap) bundle.get("data");
                    foto.setImageBitmap(img); //Trata foto tirada
                    filePatch = getImageUri(getApplicationContext(),img);
                }
            }

    }

    public String getFileExtension(Uri uri){
        //Necessário para que tanto o arquivo salvo no Storage quanto o do database tenham o mesmo nome
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public Uri getImageUri(Context inContext, Bitmap inImage){
        //Utilizo está classe para pegar o Uri da imagem, necessário no momento do Upload
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),inImage,"foto",null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        //Tratando permissões negadas

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        for(int resultado : grantResults){
            if(resultado == PackageManager.PERMISSION_DENIED){
                //Caso permissão negada
                alertaValidacaoPermissao();

            }else{
                //caso permissão aprovada
                tirarFoto();
            }
        }
    }

    public void alertaValidacaoPermissao(){
        //Mensagem exibida quando a premissão foi negada
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para tirar foto pelo app, é necessário aceitar as permissões! Caso as tenha negado deve ir na configurações de seu dispositivo e altera-las novamente!");

        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
