package com.example.despensadesonia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FIJAMOS UN IDIOMA AL INICIAR LA APLICACIÓN
        cambiarIdioma();
        //LANZAMOS EL LOGIN PARA QUE PUEDA EMPEZAR A USAR LA APP EL USUARIO
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void cambiarIdioma(){
        //EL IDIOMA BASE ES CASTELLANO, MÁS ADELANTE SE PODRÁ CAMBIAR
        Locale nuevaloc = new Locale("es");
        Locale.setDefault(nuevaloc);
        Configuration configuration =
                getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context =
                getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(
                configuration, context.getResources().getDisplayMetrics());
    }
}