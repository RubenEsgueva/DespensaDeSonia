package com.example.despensadesonia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.despensadesonia.DB.DBHelper;
import com.example.despensadesonia.DataClass.GestorIdiomas;

public class LoginActivity extends AppCompatActivity {
    private static final String KEY_USERNAME_CONTENT = "usernameContent";
    private static final String KEY_PASSWORD_CONTENT = "passwordContent";
    private static final String KEY_LANG_CONTENT = "langContent";
    private String langKey;
    private EditText inputUsr;
    private EditText inputPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("idioma")) {
            langKey = intent.getStringExtra("idioma");
        }
        GestorIdiomas gi = new GestorIdiomas();
        gi.cambiarIdioma(this, langKey);

        setContentView(R.layout.activity_login);
        //SI NOS HA LLEGADO UN IDIOMA EN LA LLAMADA LO ALMACENAMOS
        inputUsr = findViewById(R.id.username);
        inputPass = findViewById(R.id.password);
        Button loginBttn = findViewById(R.id.login);
        //SI SE HABÍA GUARDADO ALGÚN VALOR SE DEVUELVE
        if (savedInstanceState != null) {
            String savedUser = savedInstanceState.getString(KEY_USERNAME_CONTENT);
            String savedPass = savedInstanceState.getString(KEY_PASSWORD_CONTENT);
            String savedLang = savedInstanceState.getString(KEY_LANG_CONTENT);
            inputUsr.setText(savedUser);
            inputPass.setText(savedPass);
            langKey = savedLang;

        }

        loginBttn.setOnClickListener(v -> {
            String nomUsuario = inputUsr.getText().toString();
            String contrasenia = inputPass.getText().toString();
            if (comprobarLogin(nomUsuario, contrasenia)){
                //SI EL INICIO DE SESIÓN ES CORRECTO NOTIFICAMOS Y VAMOS A LA LISTA
                String welcome = getString(R.string.welcome) + nomUsuario;
                Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
                Intent i = new Intent(this, ItemListActivity.class);
                startActivity(i);
            }
            else {
                //SI NO LO ES NOTIFICAMOS Y NOS MANTENEMOS EN EL LOGIN
                Toast.makeText(getApplicationContext(), getString(R.string.loginFail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        inputUsr = findViewById(R.id.username);
        inputPass = findViewById(R.id.password);
        // GUARDAMOS EL CONTENIDO DE USUARIO Y CONTRASEÑA PARA NO PERDERLOS AL ROTAR
        outState.putString(KEY_USERNAME_CONTENT, inputUsr.getText().toString());
        outState.putString(KEY_PASSWORD_CONTENT, inputPass.getText().toString());
    }

    private boolean comprobarLogin(String usuario, String pass){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase dbLogin = dbHelper.getReadableDatabase();
        //SI EL USUARIO Y LA CONTRASEÑA COINCIDEN, ENTONCES ES CORRECTO
        Cursor cursor = dbLogin.query("t_usuarios",
                new String[]{"nombreUsuario"},
                "nombreUsuario = ? AND contrasenia = ?",
                new String[]{usuario,pass},
                null, null, null);
        boolean correcto = cursor.moveToFirst();
        cursor.close();
        return correcto;
    }
}