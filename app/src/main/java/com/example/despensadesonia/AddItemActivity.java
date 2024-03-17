package com.example.despensadesonia;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.despensadesonia.DB.DBHelper;
import com.example.despensadesonia.DataClass.GestorIdiomas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {
    private static final String KEY_LANG_CONTENT = "langContent";
    private static final String KEY_USERNAME_CONTENT = "usernameContent";
    private static final String KEY_PRODUCTNAME_CONTENT = "prodnameContent";
    private static final String KEY_CURAMOUNT_CONTENT = "curamountContent";
    private static final String KEY_MINAMOUNT_CONTENT = "minamountContent";
    private static final String KEY_LASTDATE_CONTENT = "lastdateContent";
    private static final String KEY_EXPIRDATE_CONTENT = "expirdateContent";
    private long idExiste;
    private DBHelper GestorDB;
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
        idExiste = -1;
        productName = findViewById(R.id.productNameEditText);
        currentAmount = findViewById(R.id.currentAmountEditText);
        minimumAmount = findViewById(R.id.minimumAmountEditText);
        nearestExpiry = findViewById(R.id.nearestExpiryEditText);
        lastAddedDate = findViewById(R.id.lastAddedDateEditText);

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

        if (intent != null && intent.hasExtra("articulo")){
            //COMPROBAMOS SI NOS HAN ENVIADO UN ARTÍCULO PARA PRECARGAR
            idExiste = intent.getLongExtra("articulo", -1);
            SQLiteDatabase db = GestorDB.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM t_articulos WHERE id = ?", new String[]{String.valueOf(idExiste)});
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String productNameValue = cursor.getString(cursor.getColumnIndex("nombreProducto"));
                @SuppressLint("Range") String currentAmountValue = cursor.getString(cursor.getColumnIndex("cantAct"));
                @SuppressLint("Range") String minimumAmountValue = cursor.getString(cursor.getColumnIndex("cantMin"));
                @SuppressLint("Range") String lastAddedDateValue = cursor.getString(cursor.getColumnIndex("fechaUltCompra"));
                @SuppressLint("Range") String nearestExpiryValue = cursor.getString(cursor.getColumnIndex("fechaCaducidadTop"));

                productName.setText(productNameValue);
                currentAmount.setText(currentAmountValue);
                minimumAmount.setText(minimumAmountValue);
                lastAddedDate.setText(lastAddedDateValue);
                nearestExpiry.setText(nearestExpiryValue);
            }
            cursor.close();
        }

        //AL PULSAR EL BOTÓN DE VUELTA SE REGRESA A LA LISTA
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent i = new Intent(this, ItemListActivity.class);
            i.putExtra("usuario", nomUsuario);
            i.putExtra("idioma",langKey);
            startActivity(i);
        });

        ImageButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String productNameText = productName.getText().toString().trim();
            String currentAmountText = currentAmount.getText().toString().trim();
            String minimumAmountText = minimumAmount.getText().toString().trim();
            String nearestExpiryText = nearestExpiry.getText().toString().trim();
            String lastAddedDateText = lastAddedDate.getText().toString().trim();

            if (productNameText.isEmpty()) {
                //EL TÍTULO ES UN CAMPO OBLIGATORIO
                Toast.makeText(this, getString(R.string.prodNameReq), Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                //LAS CANTIDADES DEBEN RESPETAR EL FORMATO
                double currentAmountValue = Double.parseDouble(currentAmountText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.amountReq), Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                double minimumAmountValue = Double.parseDouble(minimumAmountText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.amountReq), Toast.LENGTH_SHORT).show();
                return;
            }

            //DEFINIMOS UN FORMATO PARA LAS FECHAS
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            try {
                Date nearestExpiryDate = dateFormat.parse(nearestExpiryText);
            } catch (ParseException e) {
                Toast.makeText(this, getString(R.string.dateReq), Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Date lastAddedDate = dateFormat.parse(lastAddedDateText);
            } catch (ParseException e) {
                Toast.makeText(this, getString(R.string.dateReq), Toast.LENGTH_SHORT).show();
                return;
            }
            SQLiteDatabase bd = GestorDB.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("propietario", nomUsuario);
            values.put("nombreProducto", productNameText);
            values.put("cantAct", currentAmountText);
            values.put("cantMin", minimumAmountText);
            values.put("fechaUltCompra", lastAddedDateText);
            values.put("fechaCaducidadTop", nearestExpiryText);
            if (idExiste != -1) {
                //SI EXISTE EL ARTÍCULO LO ACTUALIZAMOS
                bd.update("t_articulos", values, "id = ?", new String[]{String.valueOf(idExiste)});
            } else {
                //SI NO EXISTE CREAMOS UNO NUEVO
                bd.insertOrThrow("t_articulos", null, values);
            }
            Toast.makeText(this, R.string.saveItemSuccess, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, ItemListActivity.class);
            i.putExtra("usuario", nomUsuario);
            i.putExtra("idioma",langKey);
            startActivity(i);
        });
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

    @Override
    protected void onDestroy() {
        //GUARDAMOS LA BASE DE DATOS EN ARCHIVOS DE TEXTO PARA MAYOR SEGURIDAD
        super.onDestroy();
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.saveDataToTextFile(this);
    }
}