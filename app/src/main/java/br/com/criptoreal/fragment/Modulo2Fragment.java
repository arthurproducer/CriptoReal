package br.com.criptoreal.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.helper.Preferencias;

/**
 * A simple {@link Fragment} subclass.
 */
public class Modulo2Fragment extends Fragment {

    private ArrayList<Aula> aulas;
    private ArrayList<Aula> aularemovida;
    private ModulosAdapter moduloAdapter;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerAulas;
    private ChildEventListener childEventListenerAulas;
    private Preferencias preferencias;
    public static final String MODULO = "Modulo 2";


    public Modulo2Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() { // Otimizando o código para melhor perfomance
        super.onStart();
        eventosFirebase();
        firebase.addChildEventListener(childEventListenerAulas);
        firebase.addValueEventListener(valueEventListenerAulas);
    }

    @Override
    public void onStop() { //Caso o usuário sai do fragmento, vamos encerrar o EventListener para ele não ficar utilizando os recursos
        super.onStop();
        eventosFirebase();
        firebase.removeEventListener(valueEventListenerAulas);
        firebase.removeEventListener(childEventListenerAulas);
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

        View view = inflater.inflate(R.layout.fragment_modulo2, container, false);

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
                .child("aulas").child(preferencias.getIdentificador()).child("Modulo 2");

        valueEventListenerAulas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                aulas.clear();

                Log.i("Status-interno", "onDataChange foi chamado!");
                if(!aularemovida.isEmpty()){
                    aulas = aularemovida;
                    aularemovida.clear();
                }else{
                if(dataSnapshot.exists()){

                    for (DataSnapshot dados : dataSnapshot.getChildren()) {//Vai pegar a Aula 1 e 2 e salvar na lista.
                        if(dados.getKey().equals("Aula 3 - Bitcoin")) {
                            String status = String.valueOf(dados.getValue());
                            aulas.add(0, new Aula(dados.getKey(), "Módulo_2_-_Aula_3_-_Bitcoin.pdf", Boolean.valueOf(status),MODULO));
                            Log.i("Status-interno", "Foi adicionado aula 3");

                        }else if(dados.getKey().equals("Aula 4 - Ethereum")){
                            String status = String.valueOf(dados.getValue());
                            aulas.add(1, new Aula(dados.getKey(), "Módulo_2_-_Aula_4__-_Ethereum.pdf", Boolean.valueOf(status),MODULO));
                            Log.i("Status-interno", "Foi adicionado aula 4");
                        }
                        moduloAdapter.notifyDataSetChanged();

                    }}else{
                    Log.i("Status-Criado","firebase é nulo!");
                    ArrayList<Aula> aulasCriadas = new ArrayList<Aula>(Arrays.asList(
                            new Aula("Aula 3 - Bitcoin", "Módulo_2_-_Aula_3_-_Bitcoin.pdf", false,MODULO),
                            new Aula("Aula 4 - Ethereum", "Módulo_2_-_Aula_4__-_Ethereum.pdf", false,MODULO)));
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

                if(dataSnapshot.getKey().equals("Aula 3 - Bitcoin")) {
                    String status = String.valueOf(dataSnapshot.getValue());
                    aularemovida.add(0, new Aula(dataSnapshot.getKey(), "Módulo_2_-_Aula_3_-_Bitcoin.pdf", Boolean.valueOf(status),MODULO));
                    Log.i("Status-interno", "Foi adicionado aula 3");

                }else if(dataSnapshot.getKey().equals("Aula 4 - Ethereum")){
                    String status = String.valueOf(dataSnapshot.getValue());
                    aularemovida.add(1, new Aula(dataSnapshot.getKey(), "Módulo_2_-_Aula_4__-_Ethereum.pdf", Boolean.valueOf(status),MODULO));
                    Log.i("Status-interno", "Foi adicionado aula 4");
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}