package br.com.criptoreal.Model;

import com.google.firebase.database.DatabaseReference;

import br.com.criptoreal.config.ConfiguracaoFirebase;

public class Aula {

    private String nome;
    private String pdf;
    private boolean status;
    private String modulo;

    public Aula(String nome, String pdf,Boolean status,String modulo) {
        this.nome = nome;
        this.pdf = pdf;
        this.status = status;
        this.modulo = modulo;
    }

    public void salvarStatus(String IdenficadorUsuarioLogado, String modulo){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFireBase();
        referenciaFirebase.child("aulas").child(IdenficadorUsuarioLogado).child(modulo).child(getNome()).setValue(this.status); //aqui é salvo o status atual da aula, se ela foi baixada ou não!
    }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getPdf() { return pdf; }

    public String getModulo() { return modulo; }

    public boolean getStatus(){ return status; }

    public void setStatus(boolean status){ this.status = status; }

}
