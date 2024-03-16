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

public class LoginActivity extends AppCompatActivity {
    private static final String KEY_USERNAME_CONTENT = "usernameContent";
    private static final String KEY_PASSWORD_CONTENT = "passwordContent";
    private EditText inputUsr;
    private EditText inputPass;
    private Button loginBttn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputUsr = findViewById(R.id.username);
        inputPass = findViewById(R.id.password);
        loginBttn = findViewById(R.id.login);

        loginBttn.setOnClickListener(v -> {
            String nomUsuario = String.valueOf(inputUsr.getText());
            String contrasenia = String.valueOf(inputPass.getText());
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