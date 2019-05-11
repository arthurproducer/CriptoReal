package br.com.criptoreal.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import br.com.criptoreal.Adapter.TabAdapter;
import br.com.criptoreal.Model.Usuario;
import br.com.criptoreal.R;
import br.com.criptoreal.config.ConfiguracaoFirebase;
import br.com.criptoreal.fragment.InicioFragment;
import br.com.criptoreal.helper.Base64Custom;
import br.com.criptoreal.helper.Preferencias;
import br.com.criptoreal.helper.SlidingTabLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificadorContato;
    private DatabaseReference referenciaFirebase;
    private DrawerLayout drawer;
    private FirebaseAuth usuarioAutenticacao;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioAutenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // metodo de suporte apenas para que funcione normalmente

        //Configurando o Navigation Menu
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHead = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);


        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sltl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        //Configurar sliding Tabs
        slidingTabLayout.setDistributeEvenly(true); // Distribui as tabs pelo espaço do layout
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.colorAccent)); // A cor que fica abaixo do nome selecionado

        //Configurar Adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);// Esse objeto recupera os fragmentos, numero de páginas e os títulos

        slidingTabLayout.setViewPager(viewPager);

        //Carregando foto e nome do Usuário
        Preferencias preferencias = new Preferencias(MainActivity.this);
        CircleImageView imgFotoUsuario = (CircleImageView) navHead.findViewById(R.id.imgNavPerfil);
        ImageView imgPerfil = (ImageView) navHead.findViewById(R.id.imgPerfil);
        TextView textNomeUsuario = (TextView) navHead.findViewById(R.id.textEditPerfil);

        //Recuperar foto e nome do UsuarioLogado
        String NomeUsuario = preferencias.getNome();
        textNomeUsuario.setText(NomeUsuario);
        String FotoUsuario = preferencias.getFotoPerfil();
        Picasso.with(MainActivity.this).load(FotoUsuario).into(imgFotoUsuario);

        imgFotoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirEditPerfil();
            }
        });
        imgPerfil.setOnClickListener(new View.OnClickListener() { //Simplificar este código
            @Override
            public void onClick(View view) {
                abrirEditPerfil();
            }
        });

    }

    public void abrirEditPerfil(){
        intent = new Intent(MainActivity.this, EditPerfilActivity.class);
        startActivity(intent);
    }

    public void abrirEditConta(){
        intent = new Intent(MainActivity.this, EditContaActivity.class);
        startActivity(intent);
    }

    public void abrirPoliticas(){
        intent = new Intent(MainActivity.this, PoliticasActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); //Utilizada para exibir os menus na tela
        inflater.inflate(R.menu.menu_investimentos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Tela de Historico
        switch(item.getItemId()){//Retorna qual menu/item foi selecionado)
            case R.id.action_settings:
                //abriroutros(); //Aqui ficará a Tela de Estudo

                intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
             return super.onOptionsItemSelected(item);}
    }

    public void abrirConvidarPessoas(){
        intent = new Intent(MainActivity.this, ConvidarActivity.class);
        startActivity(intent);
    }

    public void deslogarUsuario(){
        usuarioAutenticacao.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //Declarar uma intent e os métodos para cada caso abaixo

        switch(item.getItemId()){//Retorna qual menu/item foi selecionado)
            case R.id.item_conta:
                abrirEditConta();
                return true;
            case R.id.item_politica:
                abrirPoliticas();
                return true;
            case R.id.item_pessoas:
                abrirConvidarPessoas();
                return true;
            case R.id.item_sair:
                deslogarUsuario();
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
