package br.com.criptoreal.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import br.com.criptoreal.Adapter.ModulosAdapter;
import br.com.criptoreal.Model.Aula;
import br.com.criptoreal.R;
import br.com.criptoreal.activity.EditContaActivity;
import br.com.criptoreal.activity.LoginActivity;
import br.com.criptoreal.activity.MainActivity;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.helper.Preferencias;

public class Modulo1Fragment extends Fragment{

    private ArrayList<Aula> aulas;
    private ArrayList<Aula> aularemovida;
    private ModulosAdapter moduloAdapter;
    private Preferencias preferencias;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerAulas;
    private ChildEventListener childEventListenerAulas;
    public static final String MODULO = "Modulo 1";


    public Modulo1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() { // Otimizando o código para melhor perfomance
        super.onStart();
        //instanciaFirebase();
        eventosFirebase();
        Log.i("TESTES","onStart");
        firebase.addChildEventListener(childEventListenerAulas);
        firebase.addValueEventListener(valueEventListenerAulas);
    }

    @Override
    public void onStop() { //Caso o usuário sai do fragmento, vamos encerrar o EventListener para ele não ficar utilizando os recursos
        super.onStop();
        //instanciaFirebase();
        eventosFirebase();
        Log.i("TESTES","onStop");
        firebase.removeEventListener(childEventListenerAulas);
        firebase.removeEventListener(valueEventListenerAulas);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Recuperar instância do Firebase
        preferencias = new Preferencias(getContext());
        firebase = ConfiguracaoFirebase.getFireBase()
                .child("aulas").child(preferencias.getIdentificador());

        aulas = new ArrayList<>();
        aularemovida = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_modulo1, container, false);
        ListView listaModulos = (ListView) view.findViewById(R.id.lista_modulos);


        moduloAdapter  = new ModulosAdapter(getActivity(),aulas);
        listaModulos.setAdapter(moduloAdapter);

        eventosFirebase();

        return view;
        }

    private void eventosFirebase() {
        //Recuperar instância do Firebase
        preferencias = new Preferencias(getContext());
        firebase = ConfiguracaoFirebase.getFireBase()
                .child("aulas").child(preferencias.getIdentificador()).child("Modulo 1");
        Log.i("TESTES" , "preferencias antes do modulo1 iniciar" + preferencias.getIdentificador());

        valueEventListenerAulas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                aulas.clear();

                Log.i("TESTES" , "preferencias interna" + preferencias.getIdentificador());
                Log.i("Status-interno", "onDataChange foi chamado!");

                ///aularemovida != null
                if(!aularemovida.isEmpty()){
                    aulas = aularemovida;
                    aularemovida.clear();
                }else{
                    if(dataSnapshot.exists()){
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {//Vai pegar a Aula 1 e 2 e salvar na lista.
                        if(dados.getKey().equals("Aula 1 - Criptomoedas")) {
                            String status = String.valueOf(dados.getValue());
                            aulas.add(0, new Aula(dados.getKey(), "Módulo_1_-_Aula_1_-_Criptomoedas.pdf", Boolean.valueOf(status),MODULO));
                            Log.i("Status-interno", "Foi adicionado aula 1");

                        }else if(dados.getKey().equals("Aula 2 - Criptomoedas no Brasil")){
                            String status = String.valueOf(dados.getValue());
                            aulas.add(1, new Aula(dados.getKey(), "Módulo_1_-_Aula_2_-_Criptomoedas_no_Brasil.pdf", Boolean.valueOf(status),MODULO));
                            Log.i("Status-interno", "Foi adicionado aula 2");
                        }
                        moduloAdapter.notifyDataSetChanged();

                    }}else{
                    Log.i("Status-Criado","firebase é nulo!");
                    ArrayList<Aula> aulasCriadas = new ArrayList<Aula>(Arrays.asList(
                            new Aula("Aula 1 - Criptomoedas", "Módulo_1_-_Aula_1_-_Criptomoedas.pdf", false,MODULO),
                            new Aula("Aula 2 - Criptomoedas no Brasil", "Módulo_1_-_Aula_2_-_Criptomoedas_no_Brasil.pdf",false,MODULO)));
                    for(int pos = 0; pos < 2;pos++) {
                        aulasCriadas.get(pos).salvarStatus(preferencias.getIdentificador(),MODULO);
                        Log.i("Status-Criado","firebase recebe dados totalmente novos!");
                    }

                    moduloAdapter.atualizarItens(aulasCriadas);
                }}


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        childEventListenerAulas = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    if(aularemovida.size() == 2){
                        aularemovida.clear();
                    }


                    Log.i("TESTES" , "preferencias interna" + preferencias.getIdentificador());
                    Log.i("Status-interno", "onChildRemoved foi chamado!");
                    Log.i("TESTES","dataSnapshot " + dataSnapshot);
                    Log.i("TESTES", "dataSnapshot.getKey()"+ dataSnapshot.getKey());
                    Log.i("TESTES","aularemovida.size " + aularemovida.size());
                    Log.i("TESTES","aulas.size " + aulas.size());


                    // for (DataSnapshot dados : dataSnapshot.getChildren()) {//Vai pegar a Aula 1 e 2 e salvar na lista.
                    //while(aulas.size() <= 1){
                        if (dataSnapshot.getKey().equals("Aula 1 - Criptomoedas")) {
                            String status = String.valueOf(dataSnapshot.getValue());
                            aularemovida.add(0, new Aula(dataSnapshot.getKey(), "Módulo_1_-_Aula_1_-_Criptomoedas.pdf", Boolean.valueOf(status), MODULO));
                            Log.i("Status-interno", "Foi adicionado aula 1");

                        } else if (dataSnapshot.getKey().equals("Aula 2 - Criptomoedas no Brasil")) {
                            String status = String.valueOf(dataSnapshot.getValue());
                            aularemovida.add(1, new Aula(dataSnapshot.getKey(), "Módulo_1_-_Aula_2_-_Criptomoedas_no_Brasil.pdf", Boolean.valueOf(status), MODULO));
                            Log.i("Status-interno", "Foi adicionado aula 2");
                        }


                }


            //}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }}