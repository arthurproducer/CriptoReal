package br.com.criptoreal.fragment;


import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import br.com.criptoreal.Model.Moeda;
import br.com.criptoreal.R;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.helper.Constantes;
import br.com.criptoreal.helper.Preferencias;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversorFragment extends Fragment implements View.OnClickListener{

    private EditText editValorDigitado;
    private TextView textMoedaDigitada;
    private TextView textMoedaEscolhida;

    private ImageView imgMoedaDigitada;
    private ImageView imgMoedaEscolhida;
    private ImageView imgInversao;

    private Button btnConverter;
    private TextView textResultado;

    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerConversor;

    private enum moedaDigitadas {BTC,ETH,BRL,USD}

    final private CharSequence[]moedas = {moedaDigitadas.BTC.toString(),moedaDigitadas.ETH.toString(),moedaDigitadas.BRL.toString(),moedaDigitadas.USD.toString()};

    public ConversorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversor, container, false);

        exibindoFotoNomeUsuario(view);

        editValorDigitado = (EditText) view.findViewById(R.id.editValorDigitado);
        textMoedaDigitada = (TextView)view.findViewById(R.id.textMoedaDigitada);
        textMoedaEscolhida = (TextView) view.findViewById(R.id.textMoedaEscolhida);
        imgMoedaDigitada = (ImageView) view.findViewById(R.id.imgMoedaDigitada);
        imgMoedaEscolhida = (ImageView)  view.findViewById(R.id.imgMoedaEscolhida);
        btnConverter = (Button) view.findViewById(R.id.btnConverter);
        textResultado = (TextView) view.findViewById(R.id.textResultado);
        imgInversao = (ImageView) view.findViewById(R.id.imgInversao);

        ///DADOS INICIAIS
        textMoedaDigitada.setText(moedas[0]);
        //imgMoedaDigitada.setBackgroundResource(R.drawable.bitcoin_simbolo);
        Picasso.with(getContext()).load(Constantes.ENDERECO_FOTO_BITCOIN_SIMBOLO).into(imgMoedaDigitada);
        textMoedaEscolhida.setText(moedas[1]);
        //imgMoedaEscolhida.setBackgroundResource(R.drawable.ethereum_simbolo);
        Picasso.with(getContext()).load(Constantes.ENDERECO_FOTO_ETHEREUM_SIMBOLO).into(imgMoedaEscolhida);

        textMoedaDigitada.setOnClickListener(this);
        textMoedaEscolhida.setOnClickListener(this);
        imgMoedaDigitada.setOnClickListener(this);
        imgMoedaEscolhida.setOnClickListener(this);
        btnConverter.setOnClickListener(this);
        imgInversao.setOnClickListener(this);

        return view;
    }

    public void escolhaSuaMoeda(final ImageView image, final TextView text){

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        //configurar o titulo
        dialog.setTitle("Escolha uma das moedas abaixo!!");
        dialog.setItems(moedas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        text.setText(moedas[i]);
                        //image.setBackgroundResource(R.drawable.bitcoin_simbolo);
                        Picasso.with(getContext()).load(Constantes.ENDERECO_FOTO_BITCOIN_SIMBOLO).into(image);
                        break;
                    case 1:
                        text.setText(moedas[i]);
                        //image.setBackgroundResource(R.drawable.ethereum_simbolo);
                        Picasso.with(getContext()).load(Constantes.ENDERECO_FOTO_ETHEREUM_SIMBOLO).into(image);
                        break;
                    case 2:
                        text.setText(moedas[i]);
                        //image.setBackgroundResource(R.drawable.real_simbolo);
                        Picasso.with(getContext()).load(Constantes.ENDERECO_FOTO_REAL_SIMBOLO).into(image);
                        break;
                    case 3:
                        text.setText(moedas[i]);
                        //image.setBackgroundResource(R.drawable.dolar_simbolo);
                        Picasso.with(getContext()).load(Constantes.ENDERECO_FOTO_DOLAR_SIMBOLO).into(image);
                        break;
                }
            }
        });

        //colocando icons e cancelando a saida
        //dialog.setCancelable(false);
        dialog.setIcon(R.drawable.logo_tcc_firstclass);

        dialog.create();
        dialog.show();
    }

    public void exibindoFotoNomeUsuario(View view){
        Preferencias preferencias = new Preferencias(getContext());
        CircleImageView imgFotoUsuario = (CircleImageView) view.findViewById(R.id.imgFotoUsuario);
        TextView textNomeUsuario = (TextView) view.findViewById(R.id.textNomeUsuario);

        //Recuperar foto e nome do UsuarioLogado
        String NomeUsuario = preferencias.getNome();
        textNomeUsuario.setText(NomeUsuario);
        String FotoUsuario = preferencias.getFotoPerfil();
        Picasso.with(getContext()).load(FotoUsuario).into(imgFotoUsuario);
    }

    @Override
    public void onClick(View view) {
        if(view == textMoedaDigitada || view == imgMoedaDigitada){
            escolhaSuaMoeda(imgMoedaDigitada,textMoedaDigitada);
        }else if(view == textMoedaEscolhida || view == imgMoedaEscolhida){
            escolhaSuaMoeda(imgMoedaEscolhida,textMoedaEscolhida);
        }

        if(view == imgInversao){
          inverterMoedas();
        }

        if(view == btnConverter){

            String atualMoedaDigitada = textMoedaDigitada.getText().toString();
            String atualMoedaEscolhida = String.valueOf(textMoedaEscolhida.getText());

            if(atualMoedaEscolhida.equals(atualMoedaDigitada)){//Verifica se as duas moedas são iguais
                textResultado.setText(editValorDigitado.getText().toString());// TRATAR FORMATAÇÃO

            }else if(atualMoedaDigitada.equals(moedaDigitadas.BTC.toString())){//Verifica se MOEDA DIGITADA é BITCOIN
                    atualMoedaDigitada = "1";
                    if(atualMoedaEscolhida.equals(moedaDigitadas.ETH.toString())){//MOEDAESCOLHIDA for ETHEREUM
                        atualMoedaEscolhida = moedaDigitadas.BRL.toString();//Moeda escolhida agora é BRL
                        multiplicandoMoeda(atualMoedaDigitada,atualMoedaEscolhida,Double.valueOf(editValorDigitado.getText().toString()),moedaDigitadas.ETH.toString());
                    }else{multiplicandoMoeda(atualMoedaDigitada,atualMoedaEscolhida,Double.valueOf(editValorDigitado.getText().toString()),null);}


            }else if(atualMoedaDigitada.equals(moedaDigitadas.ETH.toString())){//Verifica se é ETHEREUM
                    atualMoedaDigitada = "1027";
                    if(atualMoedaEscolhida.equals(moedaDigitadas.BTC.toString())){//MOEDAESCOLHIDA for BITCOIN
                        atualMoedaEscolhida = moedaDigitadas.BRL.toString();//Moeda escolhida agora é BRL
                        multiplicandoMoeda(atualMoedaDigitada,atualMoedaEscolhida,Double.valueOf(editValorDigitado.getText().toString()),moedaDigitadas.BTC.toString());
                    }else{multiplicandoMoeda(atualMoedaDigitada,atualMoedaEscolhida,Double.valueOf(editValorDigitado.getText().toString()),null);}

            }else if(atualMoedaDigitada.equals(moedaDigitadas.BRL.toString())){//Verifica se é BRL

                    if(atualMoedaEscolhida.equals(moedaDigitadas.USD.toString())){//MOEDAESCOLHIDA for USD
                        textResultado.setText(NumberFormat.getCurrencyInstance(Locale.US).format(Double.valueOf(editValorDigitado.getText().toString())* 0.24358));
                    }else{ dividindoMoeda(atualMoedaDigitada,atualMoedaEscolhida,Double.valueOf(editValorDigitado.getText().toString())); }

            }else if(atualMoedaDigitada.equals(moedaDigitadas.USD.toString())){//Verifica se é USD
                    if(atualMoedaEscolhida.equals(moedaDigitadas.BRL.toString())){//MOEDAESCOLHIDA for BRL
                        Locale ptBr = new Locale("pt","BR");
                        textResultado.setText(NumberFormat.getCurrencyInstance(ptBr).format(Double.valueOf(editValorDigitado.getText().toString())* 4.105404));
                    }else{ dividindoMoeda(atualMoedaDigitada,atualMoedaEscolhida,Double.valueOf(editValorDigitado.getText().toString())); }
    }

        }
    }


    public void multiplicandoMoeda(String moedaDigitada, final String moedaEscolhida, final Double valorDigitado, final String cripto) {


            //Recuperar instância do Firebase
            firebase = ConfiguracaoFirebase.getFireBase()
                    .child("criptomoedas").child(moedaDigitada).child("moedas").child(moedaEscolhida).child("preco");

            valueEventListenerConversor = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Pega o que usuarioDigitou e multiplica pelo valor da moedaDigitada
                    Moeda moeda = new Moeda();
                    moeda.setPreco((Double) dataSnapshot.getValue());
                    try {
                        if (moedaEscolhida.equals(moedaDigitadas.USD.toString())) {// for USD
                            textResultado.setText(NumberFormat.getCurrencyInstance(Locale.US).format(moeda.getPreco() * valorDigitado));
                            removeEventListener();

                        } else if (moedaEscolhida.equals(moedaDigitadas.BRL.toString())) {//for BRL
                            if (cripto != null) {//se for uma outra CRIPTOMOEDA

                                double conversor = moeda.getPreco() * valorDigitado;

                                dividindoMoeda(moedaEscolhida, cripto, conversor);

                            } else {
                                Locale ptBr = new Locale("pt", "BR");
                                textResultado.setText(NumberFormat.getCurrencyInstance(ptBr).format(moeda.getPreco() * valorDigitado));
                                removeEventListener();
                            }
                        }
                    } catch (NullPointerException ex) {//Esse catch barra todos os bugs relacionados a troca de Fragments
                        removeEventListener();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };

        firebase.addValueEventListener(valueEventListenerConversor);
    }

    public void dividindoMoeda(String moeda, String moedaEscolhida, final Double conversor) {

        if(moedaEscolhida.equals(moedaDigitadas.BTC.toString())){
            moedaEscolhida = "1";
        }else if(moedaEscolhida.equals(moedaDigitadas.ETH.toString())){
            moedaEscolhida = "1027";
        }

        firebase = ConfiguracaoFirebase.getFireBase()
                .child("criptomoedas").child(moedaEscolhida).child("moedas").child(moeda).child("preco");

        valueEventListenerConversor = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                Moeda moeda = new Moeda();
                moeda.setPreco((Double) dataSnapshot.getValue());
                textResultado.setText(String.valueOf(conversor/moeda.getPreco()));
                //textResultado.setText(String.valueOf(Math.floor(conversor/moeda.getPreco()))); ARREDONDA PARA UM NÚMERO INTEIRO
                removeEventListener();
                }catch (NullPointerException ex){//Esse catch barra todos os bugs relacionados a troca de Fragments
                    removeEventListener();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        firebase.addValueEventListener(valueEventListenerConversor);
    }


    public void inverterMoedas(){
        String inverterTexto = textMoedaDigitada.getText().toString();
        Drawable inverterImg = imgMoedaDigitada.getDrawable();

        textMoedaDigitada.setText(textMoedaEscolhida.getText());
        imgMoedaDigitada.setImageDrawable(imgMoedaEscolhida.getDrawable());
        textMoedaEscolhida.setText(inverterTexto);
        imgMoedaEscolhida.setImageDrawable(inverterImg);}

    public void removeEventListener(){
        firebase.addValueEventListener(valueEventListenerConversor); // Aqui é adicionado o valueEvent ao Firebase o que gera uma requisição
        firebase.removeEventListener(valueEventListenerConversor);
    }

}