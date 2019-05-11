package br.com.criptoreal.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.com.criptoreal.Model.Criptomoeda;
import br.com.criptoreal.R;

public class CriptomoedaAdapter extends BaseExpandableListAdapter{

    private  ArrayList<Criptomoeda> criptomoedasGroup;
    //private HashMap<Criptomoeda, ArrayList<Criptomoeda>> criptomoedasChild;
    //private Context context;
    private LayoutInflater inflater; // ANALISAR


    public CriptomoedaAdapter(@NonNull Context c, ArrayList<Criptomoeda> criptomoedasGroup) {
        //HashMap<Criptomoeda, ArrayList<Criptomoeda>> criptomoedasChild
        this.criptomoedasGroup = criptomoedasGroup;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //this.context = c;
    }

    @Override
    public int getGroupCount() { //Tamanho da lista principal
        return criptomoedasGroup.size();
    }

    @Override
    public int getChildrenCount(int i) { //Tamanho da lista secundária
        //return criptomoedasChild.get(criptomoedasGroup.get(i)).size();
        return 1 ; // Aqui coloco 1 para que o getChildView seja repetido apenas uma vez!!
    }

    @Override
    public Object getGroup(int i) { //Pega a criptomoeda que acabou de ser inserida na lista
        return criptomoedasGroup.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        //return criptomoedasChild.get(i).get(i1);
        return criptomoedasGroup.get(i);
    }

    @Override
    public long getGroupId(int i) { //Aqui pego o ID, verificar se necessário
        return i;
    }

    @Override
    public long getChildId(int i, int i1) { //Não existe então retorna ele mesmo
        return Integer.valueOf(criptomoedasGroup.get(i1).getId());
    }

    @Override
    public boolean hasStableIds() { // É falso pois os ids(dos items) não alteram em nada
        return true;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup viewGroup) {
        Log.i("Atualizar","getGroupView" + criptomoedasGroup.get(i).getCriptomoeda());
        ViewHolderGroup holder;

        if (convertView == null) {
            //Esse trecho serve para otimizar e melhorar a performance do código, usando o viewHolder
            convertView = inflater.inflate(R.layout.lista_criptomoedas, viewGroup, false);
            holder = new ViewHolderGroup(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolderGroup) convertView.getTag();
        }

        if(criptomoedasGroup != null) {//Verifica se a lista esta vazia
            try {
                Log.i("Atualizar","criptomoedasGroup diferente de null" + criptomoedasGroup.get(i).getUltimaAtualizacao());
                Criptomoeda criptomoeda = criptomoedasGroup.get(i);

                //populando as View
                holder.nome.setText(criptomoeda.getCriptomoeda());

                Locale ptBr = new Locale("pt", "BR");
                String dinheiro = NumberFormat.getCurrencyInstance(ptBr).format(criptomoeda.getMoeda().getPreco());
                //valor.setText(String.valueOf(criptomoeda.getMoeda().getPreco()));
                holder.valor.setText(String.valueOf(dinheiro));

                Picasso.with(inflater.getContext()).load(criptomoeda.getIcone()).into(holder.image);

                holder.porcentagem.setText(String.valueOf(criptomoeda.getMoeda().getPorcentagem1h() + "%"));
                if (criptomoeda.getMoeda().getPorcentagem1h() > 0) {
                    holder.porcentagem.setTextColor(Color.GREEN);
                } else if (criptomoeda.getMoeda().getPorcentagem1h() < 0) {
                    holder.porcentagem.setTextColor(Color.RED);
                }
            }catch(NullPointerException ex){
                Toast.makeText(inflater.getContext(),"Aguardando resposta do Banco",Toast.LENGTH_SHORT).show();
            }
        }
        return convertView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        Log.i("Atualizar","getChildView" + criptomoedasGroup.get(i).getCriptomoeda());
        ViewHolderChild holder;


        if (convertView == null) { //TRECHO PARA OTIMIZAÇÃO COM DESING PATTERN VIEWHOLDER
            //Esse trecho serve para otimizar e melhorar a performance do código, usando o viewHolder
            convertView = inflater.inflate(R.layout.lista_dados_cripto, viewGroup, false);
            holder = new ViewHolderChild(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolderChild) convertView.getTag();
        }


        try {
            Criptomoeda criptomoeda = criptomoedasGroup.get(i); //Aqui é Group pois estou usando os mesmos dados tanto no Child quanto no Group

            //Picasso.with(inflater.getContext()).load(criptomoeda.getIcone()).into(holder.image);

            //populando as View
            Locale ptBr = new Locale("pt", "BR");
            String dinheiro = NumberFormat.getCurrencyInstance(ptBr).format(criptomoeda.getMoeda().getCapMercado());
            holder.capResult.setText(String.valueOf(dinheiro));
            holder.capResult.setTextColor(Color.CYAN);
            holder.circulationResult.setText(String.valueOf(criptomoeda.getEm_circulacao()));
            holder.circulationResult.setTextColor(Color.CYAN);
            holder.limiteResult.setText(String.valueOf(criptomoeda.getLimite()));
            if(holder.limiteResult.getText().equals("0")){
                holder.limiteResult.setText("Sem limites de emissão!");
            }
            holder.limiteResult.setTextColor(Color.CYAN);
            holder.diaResult.setText(String.valueOf(criptomoeda.getMoeda().getPorcentagem24h() + "%"));
            holder.diaResult.setTextColor(Color.CYAN);
            holder.horaResult.setText(String.valueOf(criptomoeda.getMoeda().getPorcentagem1h() + "%"));
            holder.horaResult.setTextColor(Color.CYAN);
            holder.semanaResult.setText(String.valueOf(criptomoeda.getMoeda().getPorcentagem7d() + "%"));
            holder.semanaResult.setTextColor(Color.CYAN);

            Log.i("Atualizar","getChildView populado" + criptomoedasGroup.get(i).getCriptomoeda());
        }catch(NullPointerException ex){
            Toast.makeText(inflater.getContext(),"Aguardando resposta do Banco",Toast.LENGTH_SHORT).show();
        }

        //if(criptomoedasChild != null) {//Verifica se a lista esta vazia//Verifica se a lista esta vazia

       // }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) { // Escuta o click
        return false;
    }

    class ViewHolderGroup{ // Serve para otimização vou pesquisar mais sobre

        TextView nome;
        TextView valor;
        ImageView image;
        TextView porcentagem;


     public ViewHolderGroup(View view){
        //pegando as referências das Views
        nome = (TextView) view.findViewById(R.id.lista_criptomoedas_nome);
        valor = (TextView) view.findViewById(R.id.lista_criptomoedas_valor);
        image = (ImageView) view.findViewById(R.id.lista_criptomoedas_imagem);
        porcentagem = (TextView) view.findViewById(R.id.lista_criptomedas_porcentagem);

    }}

    class ViewHolderChild{

        TextView capResult, circulationResult, limiteResult, diaResult, horaResult, semanaResult;

        public ViewHolderChild(View view){

            capResult = (TextView) view.findViewById(R.id.textCapResult);
            circulationResult = (TextView) view.findViewById(R.id.textCirculationResult);
            limiteResult = (TextView) view.findViewById(R.id.textMaxResult);
            diaResult = (TextView) view.findViewById(R.id.text24hResult);
            horaResult = (TextView) view.findViewById(R.id.text1hResult);
            semanaResult = (TextView) view.findViewById(R.id.text7diasResult);

        }
    }

}
