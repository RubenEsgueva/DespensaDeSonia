package com.example.despensadesonia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.despensadesonia.DB.DBHelper;
import com.example.despensadesonia.DataClass.GestorIdiomas;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FIJAMOS UN IDIOMA AL INICIAR LA APLICACIÓN
        GestorIdiomas gi = new GestorIdiomas();
        gi.cambiarIdioma(this, "es");
        //CARGAMOS LA BASE DE DATOS CON LOS ARTÍCULOS QUE PUDIERAN FALTAR
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.loadDataFromTextFile(this);
        //LANZAMOS EL LOGIN PARA QUE PUEDA EMPEZAR A USAR LA APP EL USUARIO
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("idioma", "es");
        startActivity(i);
    }
}