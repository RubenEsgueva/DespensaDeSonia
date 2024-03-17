package com.example.despensadesonia.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "despensa.db";
    public static final String TABLE_USUARIOS = "t_usuarios";
    public static final String TABLE_ARTICULOS = "t_articulos";
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //AQUÍ ALMACENAMOS LOS USUARIOS DE LA APLICACIÓN (DE MOMENTO SOLO 1)
        db.execSQL("CREATE TABLE "+TABLE_USUARIOS+"(" +
                "nombreUsuario TEXT PRIMARY KEY NOT NULL," +
                "contrasenia TEXT NOT NULL);");
        //AQUÍ ALMACENAMOS LOS PRODUCTOS PARA TODOS LOS USUARIOS
        db.execSQL("CREATE TABLE "+TABLE_ARTICULOS+"(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "propietario TEXT NOT NULL," +
                "nombreProducto TEXT NOT NULL," +
                "cantAct DECIMAL(8,2)," +
                "cantMin DECIMAL(8,2)," +
                "fechaUltCompra DATE," +
                "fechaCaducidadTop DATE)");
        //INTRODUCIMOS EL ÚNICO USUARIO DE NUESTRA APLICACIÓN
        db.execSQL("INSERT INTO "+TABLE_USUARIOS+"(" +
                "nombreUsuario, contrasenia) VALUES(" +
                "'sonia.angulo', 'sonia123')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ARTICULOS);
        onCreate(db);
    }

    public void saveDataToTextFile(Context context) {
        //GUARDAMOS EN UN ARCHIVO DE TEXTO LOS ELEMENTOS DE LA TABLA ARTÍCULOS
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ARTICULOS, null);

        try {
            OutputStreamWriter fichero = new OutputStreamWriter(context.openFileOutput("articulos.txt",
                    Context.MODE_PRIVATE));
            while (cursor.moveToNext()) {
                String line = cursor.getInt(0) + "," +   // id
                        cursor.getString(1) + "," +  // propietario
                        cursor.getString(2) + "," +  // nombreProducto
                        cursor.getDouble(3) + "," +  // cantAct
                        cursor.getDouble(4) + "," +  // cantMin
                        cursor.getString(5) + "," +  // fechaUltCompra
                        cursor.getString(6);          // fechaCaducidadTop
                fichero.write(line + "\n");
            }
            fichero.close();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public void loadDataFromTextFile(Context context) {
        //RECUPERAMOS LOS ARTÍCULOS QUE HAYA GUARDADOS EN EL DOCUMENTO A LA BASE DE DATOS
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ARTICULOS);

        try {
            BufferedReader ficherointerno = new BufferedReader(new InputStreamReader(
                    context.openFileInput("articulos.txt")));
            String line;

            while ((line = ficherointerno.readLine()) != null) {
                String[] parts = line.split(",");
                ContentValues values = new ContentValues();
                values.put("id", Integer.parseInt(parts[0]));
                values.put("propietario", parts[1]);
                values.put("nombreProducto", parts[2]);
                values.put("cantAct", Double.parseDouble(parts[3]));
                values.put("cantMin", Double.parseDouble(parts[4]));
                values.put("fechaUltCompra", parts[5]);
                values.put("fechaCaducidadTop", parts[6]);
                db.insert(TABLE_ARTICULOS, null, values);
            }

            ficherointerno.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
