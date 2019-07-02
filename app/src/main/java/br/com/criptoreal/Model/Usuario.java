package br.com.criptoreal.Model;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import br.com.criptoreal.config.ConfiguracaoFirebase;

/**
 * Classe {@link Usuario} que representa um usuário real do aplicativo.
 * @author Arthur Sales
 */
public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String confsenha;
    //private String cpf;
    private String sexo;
    private String storage_id;
    private String enderecofoto;

    public Usuario() { //Existe apenas para que o Firebase possa salvar corretamente

    }

    ///Construtor somente usado no teste
    public Usuario(String id, String nome, String email, String senha, String confsenha, String sexo, String storage_id, String enderecofoto) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.confsenha = confsenha;
        this.sexo = sexo;
        this.storage_id = storage_id;
        this.enderecofoto = enderecofoto;
    }

    public void salvar(){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFireBase();
        referenciaFirebase.child("usuarios").child(getId()).setValue(this); //ao colocar o this passamos as informações da classe usuário
        Log.i("usuario salvo", String.valueOf(this));
    }


    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    @Exclude
    public String getConfsenha() {
        return confsenha;
    }

    public void setConfsenha(String confsenha) {
        this.confsenha = confsenha;
    }
/*
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
*/
    public String getSexo() { return sexo; }

    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getStorage_id() { return storage_id; }

    public void setStorage_id(String storage_id) { this.storage_id = storage_id; }

    public String getEnderecofoto() { return enderecofoto; }

    public void setEnderecofoto(String enderecofoto) { this.enderecofoto = enderecofoto; }
}
