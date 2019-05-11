package br.com.criptoreal.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public final class ConfiguracaoFirebase {//classe não pode ser extendida

    private static DatabaseReference referenciaFirebase;// O valor será o mesmo indepetente da intancia
    private static FirebaseAuth auth; //recuperar a instancia do Firebase
    private static StorageReference storageRef;

    public static DatabaseReference getFireBase() { // não precisa instanciar(new ...)

        if (referenciaFirebase == null) {
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();//Retorna referencia
        }
        return referenciaFirebase;
    }

    public static FirebaseAuth getFirebaseAuth() { //Retorna a instancia para autenticação
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
    public static StorageReference getStorageRef(){
        if(storageRef == null){
            storageRef = FirebaseStorage.getInstance().getReference();
        }
        return storageRef;
    }

}
