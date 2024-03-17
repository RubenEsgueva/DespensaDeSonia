package com.example.despensadesonia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.despensadesonia.DB.DBHelper;
import com.example.despensadesonia.DataClass.Articulo;
import com.example.despensadesonia.DataClass.ArticuloAdapter;
import com.example.despensadesonia.DataClass.GestorIdiomas;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {
    private static final String KEY_LANG_CONTENT = "langContent";
    private static final String KEY_USERNAME_CONTENT = "usernameContent";
    private String langKey;
    private String nomUsuario;

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

        setContentView(R.layout.activity_item_list);
        //SI SE HABÍA GUARDADO ALGÚN VALOR SE DEVUELVE
        if (savedInstanceState != null) {
            String savedUser = savedInstanceState.getString(KEY_USERNAME_CONTENT);
            String savedLang = savedInstanceState.getString(KEY_LANG_CONTENT);
            nomUsuario = savedUser;
            langKey = savedLang;
        }
        //CARGAR LOS BOTONES DEL TOOLBAR
        setSupportActionBar(findViewById(R.id.toolbar));
        //NO MOSTRAMOS OTRA VEZ EL NOMBRE DE LA APLICACIÓN
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //NOTIFICAMOS CON QUÉ USUARIO SE HA INICIADO SESIÓN
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)!=
                PackageManager.PERMISSION_GRANTED) {
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
        }
        else{
            notificarSesion();
        }

        //CARGAMOS LA LISTA DE ARTÍCULOS ASOCIADOS AL USUARIO
        this.iniciarLista();
    }

    private void iniciarLista() {
        ListView itemList = findViewById(R.id.listView);
        List<Articulo> listofItems = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase bd = dbHelper.getWritableDatabase();
        //VAMOS A OBTENER TODOS LOS PRODUCTOS ASOCIADOS AL USUARIO ACTUAL
        String[] selecCols = {"id", "propietario", "nombreProducto", "cantAct", "cantMin", "fechaUltCompra", "fechaCaducidadTop"};
        String selection = "propietario = ?";
        String[] selectionArgs = {nomUsuario};
        Cursor cursor = bd.query("t_articulos", selecCols, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()){
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String propietario = cursor.getString(cursor.getColumnIndex("propietario"));
                @SuppressLint("Range") String nombreProducto = cursor.getString(cursor.getColumnIndex("nombreProducto"));
                @SuppressLint("Range") String cantAct = cursor.getString(cursor.getColumnIndex("cantAct"));
                @SuppressLint("Range") String cantMin = cursor.getString(cursor.getColumnIndex("cantMin"));
                @SuppressLint("Range") String fechaUltCompra = cursor.getString(cursor.getColumnIndex("fechaUltCompra"));
                @SuppressLint("Range") String fechaCaducidadTop = cursor.getString(cursor.getColumnIndex("fechaCaducidadTop"));
                //CREAMOS EL ARTÍCULO CON LOS DATOS OBTENIDOS Y LO AÑADIMOS A LA LISTA
                Articulo articulo = new Articulo(id, propietario, nombreProducto, cantAct, cantMin, fechaUltCompra, fechaCaducidadTop);
                listofItems.add(articulo);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        ArticuloAdapter listAdapter = new ArticuloAdapter(this, listofItems);
        itemList.setAdapter(listAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // GUARDAMOS EL CONTENIDO DE USUARIO Y EL IDIOMA ELEGIDO PARA NO PERDERLOS AL ROTAR
        outState.putString(KEY_USERNAME_CONTENT, nomUsuario);
        outState.putString(KEY_LANG_CONTENT, langKey);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.action_add) {
            //SI 'AÑADIR PRODUCTO' SE ABRE LA ACTIVIDAD CORRESPONDIENTE
            Intent intent=new Intent(this, AddItemActivity.class);
            intent.putExtra("usuario", nomUsuario);
            intent.putExtra("idioma", langKey);
            startActivity(intent);
        } else if (id == R.id.action_reminder) {
            //SI 'AÑADIR RECORDATORIO' SE ABRE GOOGLE CALENDAR
            long startMillis = System.currentTimeMillis();
            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            ContentUris.appendId(builder, startMillis);
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            //SI 'LOGOUT' REGRESAMOS AL LOGIN
            Intent intent=new Intent(this, LoginActivity.class);
            intent.putExtra("idioma", langKey);
            startActivity(intent);
        } else if (id == R.id.action_language) {
            //SI QUEREMOS CAMBIAR DE IDIOMA, SE LANZA EL DIALOG PARA ELEGIR
            if (!isFinishing()) {
                mostrarDialogCambiarIdioma();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // SI EL USUARIO AUTORIZA, RELANZAMOS LA NOTIFICACIÓN
            notificarSesion();
        }
    }

    private void mostrarDialogCambiarIdioma(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.selectLang));
        final CharSequence[] opciones = {getString(R.string.spanish),
                getString(R.string.basque), getString(R.string.english)};
        builder.setSingleChoiceItems(opciones, -1, (dialogInterface, i) -> {
            if (i == 0){
                langKey = "es";
            }
            else if (i == 1){
                langKey = "eu";
            }
            else if (i == 2){
                langKey = "en";
            }
            GestorIdiomas gi = new GestorIdiomas();
            gi.cambiarIdioma(this, langKey);
            //RECARGAMOS LA PÁGINA CON EL NUEVO IDIOMA (SIN PERDER LA SESIÓN DE USUARIO)
            Intent intent = new Intent(this, ItemListActivity.class);
            intent.putExtra("usuario", nomUsuario);
            intent.putExtra("idioma", langKey);
            startActivity(intent);
        });
        builder.show();
    }
    private void notificarSesion(){
        NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "connected");
        elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(getString(R.string.notifTitle))
                .setContentText(getString(R.string.notifDesc)+nomUsuario)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true);
        NotificationChannel elCanal = new NotificationChannel("connected", "connectedUserNotif",
                NotificationManager.IMPORTANCE_DEFAULT);
        elCanal.setDescription(getString(R.string.channelDesc));
        elCanal.enableLights(true);
        elCanal.setLightColor(Color.RED);
        elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        elCanal.enableVibration(true);
        elManager.createNotificationChannel(elCanal);
        elManager.notify(1, elBuilder.build());
    }

}