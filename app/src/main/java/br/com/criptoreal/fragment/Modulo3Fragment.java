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


public class Modulo3Fragment extends Fragment {

    private ArrayList<Aula> aulas;
    private ArrayList<Aula> aularemovida;
    private ModulosAdapter moduloAdapter;
    public static final String MODULO = "Modulo 3";
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerAulas;
    private ChildEventListener childEventListenerAulas;
    private Preferencias preferencias;

    public Modulo3Fragment() {
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
        firebase.removeEventListener(childEventListenerAulas);
        firebase.removeEventListener(valueEventListenerAulas);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        aulas = new ArrayList<>();
        aularemovida = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_modulo3, container, false);

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
                .child("aulas").child(preferencias.getIdentificador()).child("Modulo 3");

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
                        if(dados.getKey().equals("Aula 5 - ICO's")) {
                            String status = String.valueOf(dados.getValue());
                            aulas.add(0, new Aula(dados.getKey(), "Módulo_3_-_Aula_5__-_ICO_s.pdf", Boolean.valueOf(status),MODULO));
                            Log.i("Status-interno", "Foi adicionado aula 5");

                        }else if(dados.getKey().equals("Aula 6 - Investir em Criptomoedas no Brasil")){
                            String status = String.valueOf(dados.getValue());
                            aulas.add(1, new Aula(dados.getKey(), "Módulo_3_-_Aula_6__-_Investir_em_Criptomoeda_no_Brasil.pdf", Boolean.valueOf(status),MODULO));
                            Log.i("Status-interno", "Foi adicionado aula 6");
                        }
                        moduloAdapter.notifyDataSetChanged();

                    }
                }else{
                    Log.i("Status-Criado","firebase é nulo!");
                    ArrayList<Aula> aulasCriadas = new ArrayList<Aula>(Arrays.asList(
                            new Aula("Aula 5 - ICO's", "Módulo_3_-_Aula_5__-_ICO_s.pdf",false,MODULO),
                            new Aula("Aula 6 - Investir em Criptomoedas no Brasil", "Módulo_3_-_Aula_6__-_Investir_em_Criptomoeda_no_Brasil.pdf",false,MODULO)));
                    for(int pos = 0; pos < 2;pos++) {
                        aulasCriadas.get(pos).salvarStatus(preferencias.getIdentificador(),MODULO);
                        Log.i("Status-Criado","firebase recebe dados totalmente novos!");
                    }
                    moduloAdapter.atualizarItens(aulasCriadas);
                }
            }}

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


            if(dataSnapshot.getKey().equals("Aula 5 - ICO's")) {
                String status = String.valueOf(dataSnapshot.getValue());
                aularemovida.add(0, new Aula(dataSnapshot.getKey(), "Módulo_3_-_Aula_5__-_ICO_s.pdf", Boolean.valueOf(status),MODULO));
                Log.i("Status-interno", "Foi adicionado aula 5");

            }else if(dataSnapshot.getKey().equals("Aula 6 - Investir em Criptomoedas no Brasil")){
                String status = String.valueOf(dataSnapshot.getValue());
                aularemovida.add(1, new Aula(dataSnapshot.getKey(), "Módulo_3_-_Aula_6__-_Investir_em_Criptomoeda_no_Brasil.pdf", Boolean.valueOf(status),MODULO));
                Log.i("Status-interno", "Foi adicionado aula 6");
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