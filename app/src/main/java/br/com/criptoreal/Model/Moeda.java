package br.com.criptoreal.Model;

import com.google.firebase.database.DatabaseReference;

import br.com.criptoreal.config.ConfiguracaoFirebase;

public class Moeda {

    private String nomeMoeda;
    private Double preco;
    private Double volume24h;
    private Double capMercado;
    private Double porcentagem1h;
    private Double porcentagem24h;
    private Double porcentagem7d;


    public Moeda() {

    }

    public Moeda(String nomeMoeda, Double preco, Double volume24h, Double capMercado, Double porcentagem1h, Double porcentagem24h, Double porcentagem7d) {
        this.nomeMoeda = nomeMoeda;
        this.preco = preco;
        this.volume24h = volume24h;
        this.capMercado = capMercado;
        this.porcentagem1h = porcentagem1h;
        this.porcentagem24h = porcentagem24h;
        this.porcentagem7d = porcentagem7d;
    }

    public void salvarMoeda(String id){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFireBase();
        referenciaFirebase.child("criptomoedas").child(id).child("moedas").child(getNomeMoeda()).setValue(this);
    }

    public String getNomeMoeda() { return nomeMoeda; }
    public void setNomeMoeda(String nomeMoeda) { this.nomeMoeda = nomeMoeda; }

    public Double getPreco() {
        return preco;
    }
    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Double getVolume24h() {
        return volume24h;
    }
    public void setVolume24h(Double volume24h) {
        this.volume24h = volume24h;
    }

    public Double getCapMercado() { return capMercado; }
    public void setCapMercado(Double capMercado) { this.capMercado = capMercado; }

    public Double getPorcentagem1h() {
        return porcentagem1h;
    }
    public void setPorcentagem1h(Double porcentagem1h) {
        this.porcentagem1h = porcentagem1h;
    }

    public Double getPorcentagem24h() { return porcentagem24h; }
    public void setPorcentagem24h(Double porcentagem24h) { this.porcentagem24h = porcentagem24h; }

    public Double getPorcentagem7d() { return porcentagem7d; }
    public void setPorcentagem7d(Double porcentagem7d) { this.porcentagem7d = porcentagem7d; }
}
