package br.com.criptoreal.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import br.com.criptoreal.Model.Aula;
import br.com.criptoreal.R;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.helper.Constantes;
import br.com.criptoreal.helper.Preferencias;

public class ModulosAdapter extends BaseAdapter {

    private ArrayList<Aula> aulas;
    private Context context;
    private StorageReference storageFirebaseRef;
    private Preferencias preferencias;
    private AlertDialog.Builder dialog;

    public ModulosAdapter(@NonNull Context c, @NonNull ArrayList<Aula> objects) {
        this.aulas = objects;
        this.context = c;
        preferencias = new Preferencias(context);
        //this.preferencias = preferencias;
    }

    @Override
    public int getCount() { return aulas.size(); }

    @Override
    public Object getItem(int i) {
        return aulas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    } //Não sei como usa-lo



    @NonNull
    @Override
    public View getView( int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;

        if (convertView == null) {
            //Esse trecho serve para otimizar e melhorar a performance do código, usando o viewHolder
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);//Interface Global que permite você acessar serviços do sistema
            view = inflater.inflate(R.layout.lista_modulos, parent, false);


        //Verifica se a lista está vazia
        if(aulas != null){
            final Aula aula = aulas.get(position);

            storageFirebaseRef = ConfiguracaoFirebase.getStorageRef();

            ImageView iconePadrao = (ImageView) view.findViewById(R.id.iconeTextoPadrao);
            TextView txtAula = (TextView) view.findViewById(R.id.txtAula);
            final ToggleButton iconeAulaFeita = (ToggleButton) view.findViewById(R.id.iconeAulaFeita);
            final TextView txtFilePDF = (TextView) view.findViewById(R.id.textFilePDF);

            txtAula.setText(aula.getNome());
            iconeAulaFeita.setChecked(aula.getStatus());

            txtFilePDF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Acesso a aula a ser exibida
                    StorageReference AulaRef = storageFirebaseRef.child(Constantes.STORAGE_PATH_UPLOADS_AULAS + aula.getPdf());
                    txtFilePDF.setText("baixar PDF " + aula.getNome()); //Aqui pode não ficar para o projeto final, coloquei mais por motivos de teste

                    //Pego a URL pertencente a essa aula
                    AulaRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Abro uma nova intent para entrar na internet e baixar o arquivo pdf
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                    iconeAulaFeita.setChecked(true);
                    aula.setStatus(iconeAulaFeita.isChecked());
                    aula.salvarStatus(preferencias.getIdentificador(),aula.getModulo());
                }
            });

            iconeAulaFeita.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Click","onClick foi chamado");
                    if(aula.getStatus()){
                        Log.i("Click","verdadeiro");
                        dialog = new AlertDialog.Builder(context);

                        //configurar o titulo
                        dialog.setTitle("Você quer refazer esta aula?");

                        //colocando icons e cancelando a saida
                        dialog.setCancelable(false); //usuário não pode sair do dialog sem responde-lo
                        dialog.setIcon(R.drawable.logo_tcc_firstclass);

                        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                iconeAulaFeita.setChecked(aula.getStatus());
                                Toast.makeText(context, "Pressionado Botão não", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                aula.setStatus(iconeAulaFeita.isChecked());
                                Log.i("Click",String.valueOf(aula.getStatus()));
                                aula.salvarStatus(preferencias.getIdentificador(),aula.getModulo());
                                Toast.makeText(context, "Pressionado botão sim", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.create();
                        dialog.show();
                    }else {
                        Log.i("Click","falso");
                        dialog = new AlertDialog.Builder(context);

                        //configurar o titulo
                        dialog.setTitle("Você ainda não fez está aula!!  Quer faze-la?");

                        //colocando icons e cancelando a saida
                        dialog.setCancelable(false); //usuário não pode sair do dialog sem responde-lo
                        dialog.setIcon(R.drawable.logo_tcc_firstclass);

                        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                iconeAulaFeita.setChecked(aula.getStatus());
                                Toast.makeText(context, "Pressionado Botão não", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                StorageReference AulaRef = storageFirebaseRef.child(Constantes.STORAGE_PATH_UPLOADS_AULAS + aula.getPdf());

                                //Pego a URL pertencente a essa aula
                                AulaRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //Abro uma nova intent para entrar na internet e baixar o arquivo pdf
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(uri);
                                        context.startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });

                                aula.setStatus(iconeAulaFeita.isChecked());
                                Log.i("Click",String.valueOf(aula.getStatus()));
                                aula.salvarStatus(preferencias.getIdentificador(),aula.getModulo());
                                Toast.makeText(context, "Pressionado botão sim", Toast.LENGTH_SHORT).show();
                            }
                        });

                        final AlertDialog dlg = dialog.create();
                        dlg.show();
                    }
                }
            });

        }
        } else {
            view = convertView;
        }

        return view;
    }
    public void atualizarItens(ArrayList<Aula> newAulas){
        aulas.clear();
        aulas.addAll(newAulas);
        this.notifyDataSetChanged();
        Log.i("Status-externo","Nova Aula Adicionada");

    }


}