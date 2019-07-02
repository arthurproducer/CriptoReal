package br.com.criptoreal;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.criptoreal.Model.Usuario;
import br.com.criptoreal.activity.CadastroUsuarioActivity;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.helper.Base64Custom;
import br.com.criptoreal.helper.Constantes;
import br.com.criptoreal.helper.Preferencias;

/**
 * Classe de teste criada para garantir o funcionamento das principais operações
 * durante o cadastro do usuário {@link br.com.criptoreal.activity.CadastroUsuarioActivity }.
 * @
 * @author Arthur Sales
 * @date 29/06/2019
 */

@RunWith(AndroidJUnit4.class)
public class CadastroTeste {

        //Teste a serem feitos
        // Cadastro de um:
        // usuário certo
        // e-mail estranho
        // faltando dado obrigatório
        // senhas diferentes
        // senha muito curta
        // senha com mesmo caracteres
        // verificar permissão para tirar foto
        // carregar foto do celular
        // tirar foto
        // sem carregar foto
        // com a declaração ativada/desativada
        // exibir mensagem de cadastro finalizado
        // cadastro offline
        // não logar diretamente após o cadastro? Tem que passar pela tela de login?
        //



        public void setUp(){
                /* ========== Montagem do cenário ========== */

                //Objetos Firebase
                FirebaseAuth autenticacao;
                autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

                StorageReference storageFirebaseRef;
                Preferencias preferencias;

                CadastroUsuarioActivity cadastroUsuarioActivity;
                cadastroUsuarioActivity = new CadastroUsuarioActivity();

                Usuario email = new Usuario();
                email.setEmail("teste@gmail.com");
                String identificadorUsuario = Base64Custom.codificarBase64(email.getEmail());
                Usuario usuario = new Usuario(identificadorUsuario,"teste",email.getEmail(),"teste123","teste123","Masculino",null,null);
                //CHANCE DE GERAR UM NULLPOINTEXCEPTION

                preferencias = new Preferencias(cadastroUsuarioActivity.getApplicationContext());
        }


        public void shutdown(){

        }
        /**
         * Teste básico de um cadastro sem foto.
         *
         * @author Arthur Sales
         * @date 29/06/2019
         */

        public void testeCadastro() {
                /* ========== Montagem do cenário ========== */
                CadastroUsuarioActivity cadastroUsuarioActivity;
                cadastroUsuarioActivity = new CadastroUsuarioActivity();

                Usuario email = new Usuario();
                email.setEmail("teste@gmail.com");
                String identificadorUsuario = Base64Custom.codificarBase64(email.getEmail());
                Usuario usuario = new Usuario(identificadorUsuario,"teste",email.getEmail(),"teste123","teste123","Masculino",null,null);
                //CHANCE DE GERAR UM NULLPOINTEXCEPTION

                /* ========== Execução ========== */
                cadastroUsuarioActivity.verificaCampoObrigatorio();


                /* ========== Verificação ========== */
        }


        public void checkLoggedUser() {
                /* ========== Execução ========== */
                /* ========== Verificação ========== */
        }

}
