package br.com.criptoreal.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import br.com.criptoreal.config.ConfiguracaoFirebase;

public class Criptomoeda {

    private String id;
    private String criptomoeda;
    private String simbolo;
    private Moeda moeda;
    private long ultimaAtualizacao;
    private String rank;
    private String icone;
    private long em_circulacao;
    private long limite;


    public Criptomoeda(){
    }

    public void salvar(){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFireBase();
        referenciaFirebase.child("criptomoedas").child(getId()).setValue(this); //ao colocar o this passamos as informações da classe criptomedas
    }

    public Criptomoeda(String id,String criptomoeda, String simbolo,Moeda moeda, long ultimaAtualizacao, String rank,String icone, long em_circulacao, long limite) {
        super();
        this.id = id;
        this.criptomoeda = criptomoeda;
        this.simbolo = simbolo;
        this.moeda = moeda;
        this.ultimaAtualizacao = ultimaAtualizacao;
        this.rank = rank;
        this.icone = icone;
        this.em_circulacao = em_circulacao;
        this.limite = limite;
    }

    public String getId() { return id;}
    public void setId(String id) { this.id = id; }

    public String getCriptomoeda() {
        return criptomoeda;
    }
    public void setCriptomoeda(String criptomoeda) {
        this.criptomoeda = criptomoeda;
    }

    public String getSimbolo() {
        return simbolo;
    }
    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }


    //O Exclude aqui é para que os dados da criptomoeda sejam salvo sem a moeda,
    // que será adicionada somente no momento de ir ao Adapter
    @Exclude
    public Moeda getMoeda() { return moeda; }
    public void setMoeda(Moeda moeda) { this.moeda = moeda; }

    public long getUltimaAtualizacao() { return ultimaAtualizacao; }
    public void setUltimaAtualizacao(long ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }

    public String getRank() { return rank; }
    public void setRank(String rank) { rank = rank; }

    public String getIcone() {return icone; }
    public void setIcone(String icone) { this.icone = icone; }

    public long getEm_circulacao() { return em_circulacao; }
    public void setEm_circulacao(long em_circulacao) { this.em_circulacao = em_circulacao; }

    public long getLimite() { return limite; }
    public void setLimite(long limite) { this.limite = limite; }

}
