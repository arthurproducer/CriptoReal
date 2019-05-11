package br.com.criptoreal.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import br.com.criptoreal.R;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.helper.Preferencias;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EstudoFragment extends Fragment {

    private ToggleButton modulo1;
    private ToggleButton modulo2;
    private ToggleButton modulo3;
    private TextView txtStatusUsuario;
    private ProgressBar progressBar;
    private DatabaseReference firebase;
    private Preferencias preferencias;
    //private ValueEventListener valueEventListenerAulas;

    public EstudoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Recuperar instância do Firebase //VERIFICAR SE DEVE SER ÚTIL
        preferencias = new Preferencias(getContext());
        Log.i("Return-Preferences","id usuario: " + preferencias.getIdentificador() + "Preferences EstudoFragment onCreateView() :" + preferencias.getNome());
        firebase = ConfiguracaoFirebase.getFireBase()
                .child("aulas").child(preferencias.getIdentificador());

        View view = inflater.inflate(R.layout.fragment_estudo, container, false);
        // Inflate the layout for this fragment

        exibindoFotoNomeUsuario(view);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setMax(6);
        modulo1 = (ToggleButton) view.findViewById(R.id.toggleModulo1);
        modulo2 = (ToggleButton) view.findViewById(R.id.toggleModulo2);
        modulo3 = (ToggleButton) view.findViewById(R.id.toggleModulo3);
        txtStatusUsuario = (TextView) view.findViewById(R.id.txtStatusUsuario);

        tratandoButton();
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        populandoProgressBar();
    }

    public void exibindoFotoNomeUsuario(View view) {
        Preferencias preferencias = new Preferencias(getActivity());
        CircleImageView imgFotoUsuario = (CircleImageView) view.findViewById(R.id.imgFotoUsuario);
        TextView textNomeUsuario = (TextView) view.findViewById(R.id.textNomeUsuario);

        //Recuperar foto e nome do UsuarioLogado
        String NomeUsuario = preferencias.getNome();
        textNomeUsuario.setText(NomeUsuario);
        String FotoUsuario = preferencias.getFotoPerfil();
        Picasso.with(getContext()).load(FotoUsuario).into(imgFotoUsuario);
    }

    public void tratandoButton(){

        modulo1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Modulo1Fragment modulo1 = new Modulo1Fragment();
                fragmentTransaction.add(R.id.fragment, modulo1);// Adicionando o login dentro do container
                fragmentTransaction.commit();
            }
        });

        modulo2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Modulo2Fragment modulo2 = new Modulo2Fragment();
                fragmentTransaction.add(R.id.fragment, modulo2);// Adicionando o login dentro do container
                fragmentTransaction.commit();
            }
        });
        modulo3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Modulo3Fragment modulo3 = new Modulo3Fragment();
                fragmentTransaction.add(R.id.fragment, modulo3);// Adicionando o login dentro do container
                fragmentTransaction.commit();
            }
        });
    }

    public void populandoProgressBar(){

        progressBar.setProgress(0);

        DatabaseReference Modulo1Ref = firebase.child("Modulo 1");
        DatabaseReference Modulo2Ref = firebase.child("Modulo 2");
        DatabaseReference Modulo3Ref = firebase.child("Modulo 3");

        Query query1 = Modulo1Ref.orderByValue().equalTo(true);
        //Log.i("Progress - Valor Query1",String.valueOf(verificaNumModulos(query1,progressStatus)));
        verificaNumModulos(query1);
        Query query2 = Modulo2Ref.orderByValue().equalTo(true);
        //Log.i("Progress - Valor Query2",String.valueOf(verificaNumModulos(query2,verificaNumModulos(query1,progressStatus))));
        verificaNumModulos(query2);
        Query query3 = Modulo3Ref.orderByValue().equalTo(true);
        //Log.i("Progress - Valor Query3",String.valueOf(verificaNumModulos(query3,verificaNumModulos(query2,verificaNumModulos(query1,progressStatus)))));
        verificaNumModulos(query3);

        //progressBar.setProgress(Integer.valueOf(
          //verificaNumModulos(query3,verificaNumModulos(query2,verificaNumModulos(query1,progressStatus))).toString()));
    }

    public void verificaNumModulos(Query query){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long somaProgress = dataSnapshot.getChildrenCount();
                int progress = progressBar.getProgress() + Integer.valueOf(somaProgress.toString());
                progressBar.setProgress(progress);
                txtStatusUsuario.setText(progressBar.getProgress() + " de " + progressBar.getMax() + " Aulas Completas");
                //Log.i("Progress",  String.valueOf(progressBar.getProgress() + Integer.valueOf(somaProgress.toString())));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}