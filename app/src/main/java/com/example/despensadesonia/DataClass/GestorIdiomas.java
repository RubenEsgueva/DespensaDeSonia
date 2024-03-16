package com.example.despensadesonia.DataClass;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class GestorIdiomas {

    public GestorIdiomas(){

    }
    public void cambiarIdioma(Context pcontext, String clave){
        //EL IDIOMA BASE ES CASTELLANO, MÁS ADELANTE SE PODRÁ CAMBIAR
        Locale nuevaloc = new Locale(clave);
        Locale.setDefault(nuevaloc);
        Configuration configuration =
                pcontext.getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context =
                pcontext.createConfigurationContext(configuration);
        pcontext.getResources().updateConfiguration(
                configuration, context.getResources().getDisplayMetrics());
    }
}
