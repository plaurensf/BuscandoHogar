package com.example.buscandohogar.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.buscandohogar.R;
import com.example.buscandohogar.model.entity.Animal;
import com.example.buscandohogar.view.adapter.AnimalAdapter;
import com.example.buscandohogar.view.fragments.SolicitudesFragment;
import com.example.buscandohogar.view.fragments.AdoptameFragment;
import com.example.buscandohogar.view.fragments.AdoptionFragment;
import com.example.buscandohogar.view.fragments.AnimalesFragment;
import com.example.buscandohogar.view.fragments.MisMascotasFragment;
import com.example.buscandohogar.view.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PrincipalActivity extends AppCompatActivity {

    private static final String TAG = "PrincipalAnimal";


    private BottomNavigationView barNavigation;
    private FrameLayout  fragmentPrincipal;
    private SolicitudesFragment aboutusFramgent;
    private AdoptameFragment adoptameFragment;
    private AdoptionFragment adoptionFragment;
    private AnimalesFragment animalesFragment;
    private MisMascotasFragment misMascotasFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.buscandoHogarToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.logo_buscando_hogar_mini);
        setDatos();
    }

    public void setDatos(){
        barNavigation = findViewById(R.id.barNavigation);
        fragmentPrincipal = findViewById(R.id.fragmentPrincipal);
        aboutusFramgent = new SolicitudesFragment();
        adoptameFragment = new AdoptameFragment();
        adoptionFragment = new AdoptionFragment();
        animalesFragment = new AnimalesFragment(this);
        misMascotasFragment = new MisMascotasFragment();
        profileFragment = new ProfileFragment();

        setFragment(new AnimalesFragment(this));

        barNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, "onNavigationItemSelected: "+ item.getItemId());
                switch (item.getItemId()) {
                    case R.id.homeIcon:
                        setFragment(animalesFragment);
                        return true;
                    case R.id.favoriteIcon:
                        setFragment(misMascotasFragment);
                        return true;
                    case R.id.addIcon:
                        setFragment(adoptionFragment);
                        return true;
                    case R.id.profileIcon:
                        setFragment(profileFragment);
                        return true;
                    case R.id.aboutusIcon:
                        setFragment(aboutusFramgent);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    public void tomarFoto(View v){
        adoptionFragment.tomarFoto(v);
    }

    public void buscarGaleria(View v){
        adoptionFragment.buscarCarrete(v);
    }

    public void cerrarSesion(View v){
        profileFragment.cerrarSesionUsuario(v);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentPrincipal, fragment);
        fragmentTransaction.commit();
    }

    public void registrarMascota(View v) {
        adoptionFragment.registrarMascota(v);
    }
}