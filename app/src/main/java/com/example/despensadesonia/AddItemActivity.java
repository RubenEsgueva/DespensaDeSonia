package com.example.despensadesonia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.despensadesonia.DB.DBHelper;
import com.example.despensadesonia.DataClass.GestorIdiomas;

public class AddItemActivity extends AppCompatActivity {
    private static final String KEY_LANG_CONTENT = "langContent";
    private static final String KEY_USERNAME_CONTENT = "usernameContent";
    private static final String KEY_PRODUCTNAME_CONTENT = "prodnameContent";
    private static final String KEY_CURAMOUNT_CONTENT = "curamountContent";
    private static final String KEY_MINAMOUNT_CONTENT = "minamountContent";
    private static final String KEY_LASTDATE_CONTENT = "lastdateContent";
    private static final String KEY_EXPIRDATE_CONTENT = "expirdateContent";
    private DBHelper GestorDB;
    private ImageButton backButton;
    private ImageButton saveButton;
    private EditText productName;
    private EditText currentAmount;
    private EditText minimumAmount;
    private EditText nearestExpiry;
    private EditText lastAddedDate;
    private String nomUsuario;
    private String langKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("idioma")) {
            langKey = intent.getStringExtra("idioma");
        }
        if (intent != null && intent.hasExtra("usuario")) {
            nomUsuario = intent.getStringExtra("usuario");
        }
        //EN CASO DE RECARGAR LA PÁGINA, HAY QUE AJUSTAR EL IDIOMA ANTES DE FIJAR EL 'LAYOUT'
        GestorIdiomas gi = new GestorIdiomas();
        gi.cambiarIdioma(this, langKey);

        setContentView(R.layout.activity_add_item);
        GestorDB = new DBHelper(this);
        productName = findViewById(R.id.productNameEditText);
        currentAmount = findViewById(R.id.currentAmountEditText);
        minimumAmount = findViewById(R.id.minimumAmountEditText);
        nearestExpiry = findViewById(R.id.nearestExpiryEditText);
        lastAddedDate = findViewById(R.id.lastAddedDateEditText);
        //AL PULSAR EL BOTÓN DE VUELTA SE REGRESA A LA LISTA
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent i = new Intent(this, ItemListActivity.class);
            i.putExtra("usuario", nomUsuario);
            i.putExtra("idioma",langKey);
            startActivity(i);
        });
        //SI SE HABÍA GUARDADO ALGÚN VALOR SE DEVUELVE
        if (savedInstanceState != null) {
            String savedProdName = savedInstanceState.getString(KEY_PRODUCTNAME_CONTENT);
            String savedCurAmount = savedInstanceState.getString(KEY_CURAMOUNT_CONTENT);
            String savedMinAmount = savedInstanceState.getString(KEY_MINAMOUNT_CONTENT);
            String savedLastDate = savedInstanceState.getString(KEY_LASTDATE_CONTENT);
            String savedExpirDate = savedInstanceState.getString(KEY_EXPIRDATE_CONTENT);
            String savedUser = savedInstanceState.getString(KEY_USERNAME_CONTENT);
            String savedLang = savedInstanceState.getString(KEY_LANG_CONTENT);
            productName.setText(savedProdName);
            currentAmount.setText(savedCurAmount);
            minimumAmount.setText(savedMinAmount);
            nearestExpiry.setText(savedExpirDate);
            lastAddedDate.setText(savedLastDate);
            nomUsuario = savedUser;
            langKey = savedLang;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        productName = findViewById(R.id.productNameEditText);
        currentAmount = findViewById(R.id.currentAmountEditText);
        minimumAmount = findViewById(R.id.minimumAmountEditText);
        nearestExpiry = findViewById(R.id.nearestExpiryEditText);
        lastAddedDate = findViewById(R.id.lastAddedDateEditText);
        // GUARDAMOS TODOS LOS DATOS PARA NO PERDERLOS AL RECARGAR
        outState.putString(KEY_USERNAME_CONTENT, nomUsuario);
        outState.putString(KEY_LANG_CONTENT, langKey);
        outState.putString(KEY_PRODUCTNAME_CONTENT, productName.getText().toString());
        outState.putString(KEY_CURAMOUNT_CONTENT, currentAmount.getText().toString());
        outState.putString(KEY_MINAMOUNT_CONTENT, minimumAmount.getText().toString());
        outState.putString(KEY_LASTDATE_CONTENT, lastAddedDate.getText().toString());
        outState.putString(KEY_EXPIRDATE_CONTENT, nearestExpiry.getText().toString());
    }
}