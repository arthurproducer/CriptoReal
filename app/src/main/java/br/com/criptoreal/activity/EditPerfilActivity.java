package br.com.criptoreal.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.com.criptoreal.Model.Usuario;
import br.com.criptoreal.R;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.fragment.ConversorFragment;
import br.com.criptoreal.helper.Constantes;
import br.com.criptoreal.helper.Permissao;
import br.com.criptoreal.helper.Preferencias;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.criptoreal.helper.Constantes.ENDERECO_FOTO_PADRAO;

public class EditPerfilActivity extends AppCompatActivity {

    //Constante para rastrear a intent do seletor de imagens
    private static final int PICK_IMAGE_REQUEST = 234;//REQUEST necessário para pegar a foto, porém seu valor pode ser qualquer número.

    private CircleImageView imgFotoUsuario;
    private EditText textNomeUsuario;

    private Usuario usuario;
    //Uri para arquivar arquivo
    private Uri filePatch;

    private StorageReference storageFirebaseRef;

    //SOLICITAÇÃO DE PERMISSÃO
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        usuario = new Usuario();

        final CharSequence[] mudandoFoto = {
                    getString(R.string.takePhoto), getString(R.string.uploadPhoto), getString(R.string.removePhoto)};

         imgFotoUsuario = (CircleImageView) findViewById(R.id.imgNovaFoto);
         textNomeUsuario = (EditText) findViewById(R.id.editNovoNome);


        ImageButton btnNovaFoto = (ImageButton) findViewById(R.id.btnNovaFoto);
        btnNovaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditPerfilActivity.this);

                //configurar o titulo
                dialog.setTitle(R.string.choices);
                dialog.setItems(mudandoFoto, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                if(Permissao.validaPermissoes(1,EditPerfilActivity.this, permissoesNecessarias)){
                                    tirarFoto();
                                    Log.i("Tratando Foto","Permissão já tinha sido permitida ou não existe nenhuma!");
                                }
                                break;
                            case 1:
                                mostreArquivoEscolhido();
                                break;
                            case 2:
                                removerFoto();
                                break;
                        }
                    }
                });

                dialog.setIcon(R.drawable.logo_tcc_firstclass);

                dialog.create();
                dialog.show();
            }
        });

        Button btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizandoPerfil();
            }
        });

        exibindoFotoNomeUsuario();
    }

    public void exibindoFotoNomeUsuario(){
        Preferencias preferencias = new Preferencias(EditPerfilActivity.this);

        //Recuperar foto e nome do UsuarioLogado
        String NomeUsuario = preferencias.getNome();
        textNomeUsuario.setText(NomeUsuario);
        String FotoUsuario = preferencias.getFotoPerfil();
        Picasso.with(EditPerfilActivity.this).load(FotoUsuario).into(imgFotoUsuario);
    }

    public void atualizandoPerfil(){
        Log.i("Tratando Foto","botao Salvar foi clicado e FilePatch é: " + filePatch);
        Preferencias preferencias = new Preferencias(EditPerfilActivity.this);
        //OTIMIZAR ESSE TRECHO DO CÓDIGO
        usuario.setId(preferencias.getIdentificador());
        usuario.setNome(textNomeUsuario.getText().toString());
        usuario.setStorage_id(preferencias.getStorage());
        usuario.setSexo(preferencias.getSexo());

        if(filePatch != null) {//O método da foto só será chamado caso o usuário tenha colocado uma foto

       atualizandoFoto(preferencias.getFotoPerfil());
        }else{
            if(usuario.getEnderecofoto() == null){
            usuario.setEnderecofoto(preferencias.getFotoPerfil());}

            atualizarNome(preferencias);
            abrirTelaPrincipal();}
    }

    public void removerFoto(){
        Picasso.with(EditPerfilActivity.this).load(ENDERECO_FOTO_PADRAO).into(imgFotoUsuario);
        usuario.setEnderecofoto(ENDERECO_FOTO_PADRAO);
    }


    public void atualizandoFoto(String antigaFotoPerfil){
        //Excluir foto antiga do usuário do storage e salvar nova foto do usuário
        final Preferencias preferencias = new Preferencias(EditPerfilActivity.this);
        storageFirebaseRef = ConfiguracaoFirebase.getStorageRef();
        if(antigaFotoPerfil.equals(ENDERECO_FOTO_PADRAO)) { // Necessário para não apagar a foto padrão do Storage
            storageFirebaseRef.child(Constantes.STORAGE_PATH_UPLOADS + usuario.getStorage_id() + "/" + System.currentTimeMillis() + "." + getFileExtension(filePatch)).
                    putFile(filePatch).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), R.string.uploadPhotoSucess, Toast.LENGTH_SHORT).show();

                    //pegando o endereço da foto no Storage
                    usuario.setEnderecofoto(taskSnapshot.getDownloadUrl().toString());

                    atualizarNome(preferencias);
                    abrirTelaPrincipal();

                }
            }) .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.error_uploadPhoto, Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            //AQUI é feito a exclusão do qeu tem no banco em seguida é feito o upload da nova foto
            storageFirebaseRef.getStorage().getReferenceFromUrl(antigaFotoPerfil).delete().addOnSuccessListener(new OnSuccessListener<Void>() { //Deleta foto antiga
                @Override
                public void onSuccess(Void aVoid) {
                    storageFirebaseRef = ConfiguracaoFirebase.getStorageRef();
                    storageFirebaseRef.child(Constantes.STORAGE_PATH_UPLOADS + usuario.getStorage_id() + "/" + System.currentTimeMillis() + "." + getFileExtension(filePatch)).
                            putFile(filePatch).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), R.string.uploadPhotoSucess, Toast.LENGTH_SHORT).show();

                            //pegando o endereço da foto no Storage
                            usuario.setEnderecofoto(taskSnapshot.getDownloadUrl().toString());

                            atualizarNome(preferencias);
                            abrirTelaPrincipal();


                        }
                    }) .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), R.string.error_uploadPhoto, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

        }


    }

    public void atualizarNome(Preferencias preferencias){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFireBase();
        referenciaFirebase.child("usuarios").child(preferencias.getIdentificador()).child("nome").setValue(usuario.getNome());
        referenciaFirebase.child("usuarios").child(preferencias.getIdentificador()).child("enderecofoto").setValue(usuario.getEnderecofoto());
        preferencias.salvarDados(usuario.getId(),usuario.getNome(),usuario.getStorage_id(),usuario.getEnderecofoto(),usuario.getSexo());
        Log.i("Tratando Foto","Veja como ficou as preferencias" + preferencias.getIdentificador() + preferencias.getNome() + preferencias.getFotoPerfil());
        Log.i("Tratando Foto","Veja como ficou o usuario" + usuario.getId() + usuario.getNome() + usuario.getEnderecofoto());
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
                imgFotoUsuario.setImageBitmap(bitmap);//aqui é definido qual das imagens vai receber o método mostrarArquivoEscolhido
            }catch (IOException e){
                e.printStackTrace();
            }}

        else if(data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                Bitmap img = (Bitmap) bundle.get("data");
                imgFotoUsuario.setImageBitmap(img); //Trata foto tirada
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){//Tratando permissões negadas

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        for(int resultado : grantResults){
            if(resultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();

            }else{
                tirarFoto();
                Log.i("Tratando Foto","botao tirarFoto foi clicado e FilePatch é: " + filePatch);
            }
        }
    }

    public void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permissionsDenied);
        builder.setMessage(R.string.permissionsMessage);

        builder.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void abrirTelaPrincipal(){
        Intent intent = new Intent(EditPerfilActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
