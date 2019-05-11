
package br.com.criptoreal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import br.com.criptoreal.Adapter.CriptomoedaAdapter;
import br.com.criptoreal.Adapter.TabAdapter;
import br.com.criptoreal.Model.Criptomoeda;
import br.com.criptoreal.Model.Moeda;
import br.com.criptoreal.R;
import br.com.criptoreal.activity.MainActivity;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.config.HttpConnections;
import br.com.criptoreal.helper.Preferencias;
import de.hdodenhof.circleimageview.CircleImageView;

import static br.com.criptoreal.helper.Constantes.ENDERECO_FOTO_BITCOIN;
import static br.com.criptoreal.helper.Constantes.ENDERECO_FOTO_ETHEREUM;
import static br.com.criptoreal.helper.Constantes.URL_API_BITCOIN;
import static br.com.criptoreal.helper.Constantes.URL_API_ETHEREUM;

/**
 * A simple {@link Fragment} subclass.
 */
public class InicioFragment extends Fragment{

    private CriptomoedaAdapter  criptoAdapter;
    private ArrayList<Criptomoeda> criptomoedas;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerCriptomoedas;
    private TextView textHora;
    private ImageButton btnAtualizar;


    public InicioFragment() {
        // É necessário um fragment vazio
    }

    @Override
    public void onStart() { // Otimizando o código para melhor perfomance
        super.onStart();
        Log.i("Atualizar","onStart");
        firebase.addValueEventListener(valueEventListenerCriptomoedas);
    }


    @Override
    public void onStop() { //Caso o usuário sai do fragmento, vamos encerrar o EventListener para ele não ficar utilizando os recursos
        super.onStop();
        Log.i("Atualizar","onStop");
        firebase.removeEventListener(valueEventListenerCriptomoedas);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Montagem da View, para ser exibida dentro do MainActivity
        //callEthereumDados();

        Log.i("Atualizar","onCreateView");
        criptomoedas = new ArrayList<>();


        // Inflate o Layout para esse Fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        textHora = (TextView) view.findViewById(R.id.textHora);
        btnAtualizar = (ImageButton) view.findViewById(R.id.btnAtualizar);
        exibindoFotoNomeUsuario(view);

        Log.i("Atualizar","Antes do Expandable");
        //Montando ListView
        final ExpandableListView listaCriptomoedas = (ExpandableListView) view.findViewById(R.id.lista_criptomoedas);
        listaCriptomoedas.setGroupIndicator(null);

        criptoAdapter = new CriptomoedaAdapter(getActivity(),criptomoedas); // Utilizando nosso Adapter criado
        listaCriptomoedas.setAdapter(criptoAdapter);

        Log.i("Atualizar","Expandable criado");
        callBitcoinDados();//Passando os dados para o banco
        callEthereumDados();

        //Recuperar instância do Firebase
        firebase = ConfiguracaoFirebase.getFireBase()
                .child("criptomoedas");


            //Listener para recuperar as Criptomoedas
            valueEventListenerCriptomoedas = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) { // Chamado sempre que os dados forem alterados

                    //Limpar Lista
                    criptomoedas.clear();//Limpo para que sempre adicione apenas os listados, não acumulando criptomedas

                    //Listar contatos
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {//Pega tudo que está dentro da Criptomoeda no banco
                        Criptomoeda criptomoeda = dados.getValue(Criptomoeda.class);//Recupera todos os dados da Criptomoeda, Exceto a sua moeda
                        criptomoeda.setMoeda(dados.child("moedas").child("BRL").getValue(Moeda.class));//Pego os valores da moeda BRL
                        criptomoedas.add(criptomoeda);

                        Date date = new Date(criptomoeda.getUltimaAtualizacao()*1000L);
                        SimpleDateFormat jdf = new SimpleDateFormat("HH:mm:ss");
                        jdf.setTimeZone(TimeZone.getTimeZone("GMT-2"));
                        textHora.setText(String.valueOf(jdf.format(date)));
                        Log.i("Atualizar","Exibe ultima atualização" + criptomoeda.getUltimaAtualizacao());

                        Log.i("Atualizar","Pego os dados do Firebase" + criptomoeda.getCriptomoeda());


                    }
                    criptoAdapter.notifyDataSetChanged();//Informa nosso adapter que os dados mudaram
                    Log.i("Atualizar","Notificou que os dados mudaram");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atualizarDados();
            }
        });

        return view;
    }

    private void exibindoFotoNomeUsuario(View view){
        Preferencias preferencias = new Preferencias(getContext());
        CircleImageView imgFotoUsuario = (CircleImageView) view.findViewById(R.id.imgFotoUsuario);
        TextView textNomeUsuario = (TextView) view.findViewById(R.id.txtNomeUsuario);

        //Recuperar foto e nome do UsuarioLogado
        String NomeUsuario = preferencias.getNome();
        textNomeUsuario.setText(NomeUsuario);
        String FotoUsuario = preferencias.getFotoPerfil();
        Picasso.with(getContext()).load(FotoUsuario).into(imgFotoUsuario);
    }

    private void leituraJSON(String data){
        Log.i("Atualizar","entrou no leituraJSON" + data);
        Criptomoeda criptomoeda = new Criptomoeda();

        try {
            JSONObject obj = new JSONObject(data);
            JSONObject criptoDados = obj.getJSONObject("data");
            JSONObject quotes = criptoDados.getJSONObject("quotes");
            JSONObject dadosBRL = quotes.getJSONObject("BRL");
            JSONObject dadosUSD = quotes.getJSONObject("USD"); //Será usado na tela de conversão

                criptomoeda.setId(criptoDados.getString("id"));
                criptomoeda.setCriptomoeda(criptoDados.getString("name"));
                criptomoeda.setSimbolo(criptoDados.getString("symbol"));
                criptomoeda.setRank(criptoDados.getString("rank"));
                criptomoeda.setEm_circulacao(criptoDados.getLong( "circulating_supply"));
                if(criptoDados.isNull("max_supply")){
                    criptomoeda.setLimite(0);
                }else{
                    criptomoeda.setLimite(criptoDados.getLong("max_supply"));
                }


                if(criptomoeda.getId().equals("1")){ //TEMPORÁRIO
                    criptomoeda.setIcone(ENDERECO_FOTO_BITCOIN);
                }else if(criptomoeda.getId().equals("1027")){
                    criptomoeda.setIcone(ENDERECO_FOTO_ETHEREUM);
                }
                criptomoeda.setUltimaAtualizacao(criptoDados.getLong("last_updated"));

            Moeda moedaBRL = new Moeda(
                        "BRL",
                        dadosBRL.getDouble("price"),
                        dadosBRL.getDouble("volume_24h"),
                        dadosBRL.getDouble("market_cap"),
                        dadosBRL.getDouble("percent_change_1h"),
                        dadosBRL.getDouble("percent_change_24h"),
                        dadosBRL.getDouble("percent_change_7d"));

                Moeda moedaUSD = new Moeda(
                        "USD",
                        dadosUSD.getDouble("price"),
                        dadosUSD.getDouble("volume_24h"),
                        dadosUSD.getDouble("market_cap"),
                        dadosUSD.getDouble("percent_change_1h"),
                        dadosUSD.getDouble("percent_change_24h"),
                        dadosUSD.getDouble("percent_change_7d")
                );
                //criptomoeda.setMoeda(moedaBRL);
            criptomoeda.salvar(); //Salvando no Firebase
            Log.i("Atualizar","Salvo os dados no Firebase" + criptomoeda.getCriptomoeda());
            moedaBRL.salvarMoeda(criptomoeda.getId());//Salvo a os preços em Reais no banco
            moedaUSD.salvarMoeda(criptomoeda.getId());//Salvo os preços em Dolar no banco

        }catch (JSONException E){
            E.printStackTrace();}
    }

    public void callBitcoinDados() {
        Log.i("Atualizar","callBitcoin antes Thread");

        new Thread() {
            public void run() {

                String resposta = HttpConnections.get(URL_API_BITCOIN);

                Log.i("Atualizar","callBitcoin dentro da Thread");

                leituraJSON(resposta);

            }

        }.start();

    }

    public void callEthereumDados() {
        Log.i("Atualizar","callEthereum antes da Thread");
        new Thread() {
            public void run() {

                String resposta = HttpConnections.get(URL_API_ETHEREUM);

                Log.i("Atualizar","callEthereum dentro da Thread");


                leituraJSON(resposta);

            }
        }.start();
    }


    public void atualizarDados() {// VOU APENAS ATUALIZAR O INICIOFRAGMENT
        Toast.makeText(getContext(),"Atualizado com sucesso!!",Toast.LENGTH_LONG).show();

        //Reiniciando o MainActivity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

    }

}
